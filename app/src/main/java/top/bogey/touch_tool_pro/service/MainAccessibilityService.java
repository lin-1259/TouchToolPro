package top.bogey.touch_tool_pro.service;

import static top.bogey.touch_tool_pro.ui.InstantActivity.ACTION_ID;
import static top.bogey.touch_tool_pro.ui.InstantActivity.TASK_ID;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.RestartType;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.action.start.TimeStartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.TaskRunningListener;
import top.bogey.touch_tool_pro.bean.task.TaskWorker;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.ui.PermissionActivity;
import top.bogey.touch_tool_pro.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool_pro.ui.custom.ToastFloatView;
import top.bogey.touch_tool_pro.ui.custom.TouchPathFloatView;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.ResultCallback;
import top.bogey.touch_tool_pro.utils.SettingSave;
import top.bogey.touch_tool_pro.utils.TaskQueue;
import top.bogey.touch_tool_pro.utils.TaskThreadPoolExecutor;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

public class MainAccessibilityService extends AccessibilityService {
    // 服务
    public static final MutableLiveData<Boolean> serviceConnected = new MutableLiveData<>(false);
    public static final MutableLiveData<Boolean> serviceEnabled = new MutableLiveData<>(false);
    // 截屏
    public static final MutableLiveData<Boolean> captureEnabled = new MutableLiveData<>(false);
    // 任务
    public final ExecutorService taskService = new TaskThreadPoolExecutor(5, 30, 30, TimeUnit.SECONDS, new TaskQueue<>(20));
    private final Set<TaskRunnable> runnableSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<TaskRunningListener> listeners = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final HashSet<EnterActivityListener> enterActivityListeners = new HashSet<>();
    public ResultCallback captureResultCallback;
    private SystemEventReceiver systemEventReceiver;
    private MainCaptureService.CaptureServiceBinder binder = null;
    private ServiceConnection connection = null;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null) return;

        String packageName = (String) event.getPackageName();
        String className = (String) event.getClassName();
        if (packageName == null || className == null) return;
        Log.d("TAG", "onAccessibilityEvent: " + packageName + "/" + className);

        WorldState worldState = WorldState.getInstance();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (worldState.enterActivity(packageName, className)) {
                enterActivityListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onEnterActivity(packageName, className));
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            if (!className.contains(Notification.class.getSimpleName())) return;

            List<CharSequence> eventText = event.getText();
            if (eventText.isEmpty()) return;
            StringBuilder builder = new StringBuilder();
            for (CharSequence charSequence : eventText) {
                builder.append(charSequence);
                builder.append("\n");
            }
            worldState.setNotification(packageName, builder.toString().trim());
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        serviceConnected.setValue(true);
        setServiceEnabled(SettingSave.getInstance().isServiceEnabled());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceConnected.setValue(false);
        return super.onUnbind(intent);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.getInstance().setService(this);
        systemEventReceiver = new SystemEventReceiver();
        registerReceiver(systemEventReceiver, systemEventReceiver.getFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (systemEventReceiver != null) unregisterReceiver(systemEventReceiver);

        serviceConnected.setValue(false);
        setServiceEnabled(false);
        MainApplication.getInstance().setService(null);
    }

    public boolean isServiceConnected() {
        return Boolean.TRUE.equals(serviceConnected.getValue());
    }

    public boolean isServiceEnabled() {
        return isServiceConnected() && Boolean.TRUE.equals(serviceEnabled.getValue());
    }

    public void setServiceEnabled(boolean enabled) {
        serviceEnabled.setValue(enabled);
        SettingSave.getInstance().setServiceEnabled(enabled);

        if (isServiceEnabled()) {
            WorldState.getInstance().resetAppMap(this);
            for (Task task : SaveRepository.getInstance().getTasksByStart(TimeStartAction.class)) {
                for (Action action : task.getActionsByClass(TimeStartAction.class)) {
                    TimeStartAction startAction = (TimeStartAction) action;
                    if (startAction.isEnable()) {
                        addWork(task, startAction);
                    }
                }
            }
        } else {
            WorkManager.getInstance(this).cancelAllWork();
            stopAllTask();
        }
    }

    public void addEnterListener(EnterActivityListener listener) {
        enterActivityListeners.add(listener);
    }

    public void removeEnterListener(EnterActivityListener listener) {
        enterActivityListeners.remove(listener);
    }

    //-----------------------------------任务----------------------------------

    public void addListener(TaskRunningListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TaskRunningListener listener) {
        listeners.remove(listener);
    }

    public void runTask(Task task, StartAction startAction) {
        FunctionContext copy = (FunctionContext) task.copy();
        if (copy == null) return;
        runTask(task, startAction, copy);
    }

    public TaskRunnable runTask(Task task, StartAction startAction, FunctionContext context) {
        return runTask(task, startAction, context, null);
    }

    public TaskRunnable runTask(Task task, StartAction startAction, FunctionContext context, TaskRunningListener listener) {
        if (task == null || startAction == null || context == null) return null;
        if (!isServiceEnabled()) return null;

        if (startAction.getRestartType() == RestartType.CANCEL) {
            if (isTaskRunning(task, startAction)) return null;
        }

        TaskRunnable runnable = new TaskRunnable(task, context, startAction);
        if (listener != null) runnable.addListener(listener);
        runnable.addListener(new TaskRunningListener() {
            @Override
            public void onStart(TaskRunnable runnable) {
                if (startAction.getRestartType() == RestartType.RESTART) {
                    stopTask(task);
                }
                runnableSet.add(runnable);
            }

            @Override
            public void onEnd(TaskRunnable runnable) {
                runnableSet.remove(runnable);
            }

            @Override
            public void onProgress(TaskRunnable runnable, Action action, int progress) {
            }
        });
        listeners.stream().filter(Objects::nonNull).forEach(runnable::addListener);

        Future<?> future = taskService.submit(runnable);
        runnable.setFuture(future);
        return runnable;
    }

    public boolean isTaskRunning(Task task) {
        if (task == null) return false;
        for (TaskRunnable taskRunnable : runnableSet) {
            if (task.getId().equals(taskRunnable.getTask().getId())) {
                return !taskRunnable.isInterrupt();
            }
        }
        return false;
    }

    public boolean isTaskRunning(Task task, StartAction startAction) {
        if (startAction == null) return isTaskRunning(task);
        for (TaskRunnable taskRunnable : runnableSet) {
            if (task.getId().equals(taskRunnable.getTask().getId()) && startAction.getId().equals(taskRunnable.getStartAction().getId())) {
                return !taskRunnable.isInterrupt();
            }
        }
        return false;
    }

    public void stopTask(Task task) {
        for (TaskRunnable taskRunnable : runnableSet) {
            if (task.getId().equals(taskRunnable.getTask().getId())) {
                taskRunnable.stop();
            }
        }
    }

    public void stopAllTask() {
        for (TaskRunnable taskRunnable : runnableSet) {
            taskRunnable.stop();
        }
        runnableSet.clear();
    }

    //-----------------------------------录屏----------------------------------
    public void callStartCaptureFailed() {
        if (captureResultCallback != null) captureResultCallback.onResult(false);
    }

    public void bindCaptureService(boolean result, Intent data) {
        Log.d("TAG", "bindCaptureService: " + result);
        if (result) {
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    binder = (MainCaptureService.CaptureServiceBinder) service;
                    captureEnabled.setValue(true);
                    if (captureResultCallback != null) captureResultCallback.onResult(true);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    stopCaptureService();
                }
            };
            Intent intent = new Intent(this, MainCaptureService.class);
            intent.putExtra(MainCaptureService.DATA, data);
            result = bindService(intent, connection, Context.BIND_AUTO_CREATE);
            if (!result) {
                callStartCaptureFailed();
            }
        } else {
            callStartCaptureFailed();
        }
    }

    public void startCaptureService(boolean moveBack, ResultCallback callback) {
        if (binder == null) {
            captureResultCallback = callback;
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.putExtra(PermissionActivity.INTENT_KEY_START_CAPTURE, true);
            intent.putExtra(PermissionActivity.INTENT_KEY_MOVE_BACK, moveBack);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            if (callback != null) callback.onResult(true);
        }
    }

    public void stopCaptureService() {
        if (connection != null) {
            unbindService(connection);
            connection = null;
            stopService(new Intent(this, MainCaptureService.class));
        }
        binder = null;
        captureEnabled.postValue(false);
    }

    //-----------------------------------录屏图片----------------------------------
    public boolean isCaptureEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return true;
        return isServiceEnabled() && Boolean.TRUE.equals(captureEnabled.getValue());
    }

    public synchronized Bitmap getCurrImage() {
        if (!isCaptureEnabled()) return null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AtomicReference<Bitmap> bitmapReference = new AtomicReference<>(null);
            CountDownLatch latch = new CountDownLatch(1);
            takeScreenshot(0, Executors.newSingleThreadExecutor(), new TakeScreenshotCallback() {
                @Override
                public void onSuccess(@NonNull ScreenshotResult screenshot) {
                    Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshot.getHardwareBuffer(), screenshot.getColorSpace());
                    // 复制bitmap
                    if (bitmap != null) {
                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                        bitmapReference.set(bitmap);
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(int errorCode) {
                    latch.countDown();
                }
            });
            try {
                latch.await();
                return bitmapReference.get();
            } catch (InterruptedException ignored) {
                return null;
            }
        } else {
            return binder.getCurrImage();
        }
    }

    //-----------------------------------定时----------------------------------

    public void addWork(Task task, TimeStartAction startAction) {
        if (task == null || startAction == null) return;
        if (!isServiceEnabled()) return;

        WorkManager workManager = WorkManager.getInstance(this);
        long timeMillis = System.currentTimeMillis();
        long startTime = startAction.getStartTime();
        long periodic = startAction.getPeriodic();

        if (periodic > 0) {
            long nextStartTime;
            long l = timeMillis - startTime;
            // 当前时间没达到定时时间，下次执行时间就是开始时间
            if (l < 0) nextStartTime = startTime;
            else {
                // 当前时间大于开始时间，需要计算下次开始的时间，防止定时任务刚设定就执行了
                int loop = (int) Math.floor(l * 1f / periodic);
                nextStartTime = startTime + loop * periodic;
            }

            // 定时任务执行窗口时间，会在这个窗口时间任意时间执行
            final long flexInterval = 5 * 60 * 1000;
            // 带间隔的定时任务需要初始的时间在间隔前
            long initDelay = nextStartTime - timeMillis + flexInterval;
            // 尽量小延迟的执行间隔任务
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(TaskWorker.class, periodic, TimeUnit.MILLISECONDS, flexInterval, TimeUnit.MILLISECONDS)
                    .setInitialDelay(initDelay, TimeUnit.MILLISECONDS)
                    .setInputData(new Data.Builder()
                            .putString(TASK_ID, task.getId())
                            .putString(ACTION_ID, startAction.getId())
                            .build())
                    .build();
            workManager.enqueueUniquePeriodicWork(startAction.getId(), ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest);

            SaveRepository.getInstance().addLog(task.getId(), startAction.getFullDescription() +
                    getString(R.string.periodic_work_add,
                            getString(R.string.date,
                                    AppUtils.formatDateLocalDate(this, startTime),
                                    AppUtils.formatDateLocalTime(this, startTime)),
                            AppUtils.formatDateLocalDuration(this, periodic)
                    )
            );
        } else {
            if (startTime < timeMillis) return;

            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskWorker.class)
                    .setInitialDelay(startTime - timeMillis, TimeUnit.MILLISECONDS)
                    .setInputData(new Data.Builder()
                            .putString(TASK_ID, task.getId())
                            .putString(ACTION_ID, startAction.getId())
                            .build())
                    .build();
            workManager.enqueueUniqueWork(startAction.getId(), ExistingWorkPolicy.REPLACE, workRequest);

            SaveRepository.getInstance().addLog(task.getId(), startAction.getFullDescription() +
                    getString(R.string.work_add,
                            getString(R.string.date,
                                    AppUtils.formatDateLocalDate(this, startTime),
                                    AppUtils.formatDateLocalTime(this, startTime))
                    )
            );
        }
    }

    public void replaceWork(Task task) {
        ArrayList<Action> startActions = task.getActionsByClass(TimeStartAction.class);
        Task originTask = SaveRepository.getInstance().getOriginTaskById(task.getId());
        WorkManager workManager = WorkManager.getInstance(this);

        if (originTask != null) {
            originTask.getActionsByClass(TimeStartAction.class).forEach(action -> {
                TimeStartAction timeStartAction = (TimeStartAction) action;
                if (!timeStartAction.isEnable()) return;

                boolean exist = false;
                for (Action startAction : startActions) {
                    if (timeStartAction.getId().equals(startAction.getId())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    workManager.cancelUniqueWork(action.getId());
                }
            });
        }

        startActions.forEach(action -> {
            TimeStartAction timeStartAction = (TimeStartAction) action;
            if (timeStartAction.isEnable()) {
                // 添加新的定时任务，覆盖之前设置的
                addWork(task, timeStartAction);
            } else {
                workManager.cancelUniqueWork(action.getId());
            }
        });
    }

    public void showToast(String msg) {
        KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
        if (keepView != null) {
            new Handler(Looper.getMainLooper()).post(() -> {
                ToastFloatView view = (ToastFloatView) EasyFloat.getView(ToastFloatView.class.getCanonicalName());
                if (view == null) {
                    view = new ToastFloatView(keepView.getContext());
                    view.show();
                }
                view.showToast(msg);
            });
        }
    }

    public void showTouch(PinTouch touch, float scale) {
        if (!SettingSave.getInstance().isShowTouch()) return;

        KeepAliveFloatView keepView = MainApplication.getInstance().getKeepView();
        if (keepView != null) {
            new Handler(Looper.getMainLooper()).post(() -> new TouchPathFloatView(keepView.getContext(), touch, scale).show());
        }
    }

    @SuppressLint("DefaultLocale")
    public void showTouch(int x, int y) {
        if (!SettingSave.getInstance().isShowTouch()) return;
        PinTouch pinTouch = new PinTouch(this, new ArrayList<>(Collections.singleton(new PinTouch.TouchRecord(String.format("500;[%d.%d.0]", x, y)))));
        showTouch(pinTouch, 1);
    }

    public ArrayList<AccessibilityNodeInfo> getNeedWindowsRoot() {
        ArrayList<AccessibilityNodeInfo> roots = new ArrayList<>();
        List<AccessibilityWindowInfo> windows = getWindows();
        for (AccessibilityWindowInfo window : windows) {
            if (window == null) continue;
            if (window.getType() == AccessibilityWindowInfo.TYPE_ACCESSIBILITY_OVERLAY) continue;
            AccessibilityNodeInfo root = window.getRoot();
            if (root == null) continue;
            if (root.getChildCount() > 0) roots.add(root);
        }
        return roots;
    }

    public void runGesture(int x, int y, int time, ResultCallback callback) {
        if (x >= 0 && y >= 0 && time > 0) {
            Path path = new Path();
            path.moveTo(x, y);
            runGesture(new HashSet<>(Collections.singletonList(new GestureDescription.StrokeDescription(path, 0, time))), callback);
            return;
        }
        if (callback != null) callback.onResult(false);
    }

    public void runGesture(HashSet<GestureDescription.StrokeDescription> strokes, ResultCallback callback) {
        if (strokes == null || strokes.isEmpty()) {
            if (callback != null) callback.onResult(false);
            return;
        }

        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (GestureDescription.StrokeDescription stroke : strokes) {
            builder.addStroke(stroke);
        }
        dispatchGesture(builder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                if (callback != null) callback.onResult(true);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                if (callback != null) callback.onResult(false);
            }
        }, null);
    }

    public void runGesture(ArrayList<HashSet<GestureDescription.StrokeDescription>> strokeList, ResultCallback callback) {
        new GestureHandler(strokeList, callback).dispatchGesture();
    }

    private class GestureHandler {
        private final ArrayList<HashSet<GestureDescription.StrokeDescription>> strokeList;
        private final ResultCallback callback;

        public GestureHandler(ArrayList<HashSet<GestureDescription.StrokeDescription>> strokeList, ResultCallback callback) {
            this.strokeList = strokeList;
            this.callback = callback;
        }

        private void dispatchGesture() {
            if (strokeList.isEmpty()) {
                if (callback != null) callback.onResult(true);
                return;
            }
            runGesture(strokeList.remove(0), result -> {
                if (result) {
                    dispatchGesture();
                } else {
                    if (callback != null) callback.onResult(false);
                }
            });
        }
    }
}
