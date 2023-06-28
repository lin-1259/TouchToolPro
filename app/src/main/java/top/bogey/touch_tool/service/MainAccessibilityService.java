package top.bogey.touch_tool.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Path;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.TaskWorker;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.start.OutStartAction;
import top.bogey.touch_tool.data.action.start.RestartType;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.data.action.start.TimeStartAction;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.receiver.BatteryReceiver;
import top.bogey.touch_tool.ui.InstantActivity;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.ui.PermissionActivity;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.ResultCallback;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.TaskQueue;
import top.bogey.touch_tool.utils.TaskRunningCallback;
import top.bogey.touch_tool.utils.TaskThreadPoolExecutor;

public class MainAccessibilityService extends AccessibilityService {
    private BatteryReceiver batteryReceiver;

    // 服务
    public static final MutableLiveData<Boolean> serviceConnected = new MutableLiveData<>(false);
    public static final MutableLiveData<Boolean> serviceEnabled = new MutableLiveData<>(false);

    // 截屏
    public static final MutableLiveData<Boolean> captureEnabled = new MutableLiveData<>(false);
    public MainCaptureService.CaptureServiceBinder binder = null;
    private ServiceConnection connection = null;
    public ResultCallback captureResultCallback;

    // 任务
    public final ExecutorService taskService = new TaskThreadPoolExecutor(5, 30, 5L, TimeUnit.MINUTES, new TaskQueue<>(20));
    private final Set<TaskRunnable> runnableSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<TaskRunningCallback> callbacks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event != null) {
                String packageName = (String) event.getPackageName();
                String className = (String) event.getClassName();
                if (packageName == null || className == null) return;
                Log.d("TAG", "onAccessibilityEvent: " + packageName + "|" + className);

                WorldState worldState = WorldState.getInstance();
                if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                    if (getPackageName().contentEquals(packageName) && worldState.isActivityClass(packageName, className)) {
                        worldState.setEnterActivity(packageName, className);
                    } else worldState.enterActivity(packageName, className);

                } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                    if (!Notification.class.getName().contentEquals(className)) return;
                    List<CharSequence> eventText = event.getText();
                    if (eventText.size() == 0) return;
                    StringBuilder builder = new StringBuilder();
                    for (CharSequence charSequence : eventText) {
                        builder.append(charSequence);
                        builder.append(" ");
                    }
                    worldState.setNotification(packageName, builder.toString().trim());
                }
            }
        } catch (Exception e) {
            Log.d("TAG", "onAccessibilityEvent: " + "Error");
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
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, batteryReceiver.getFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) unregisterReceiver(batteryReceiver);
        stopCaptureService();
        stopAllTask();

        serviceConnected.setValue(false);
        serviceEnabled.setValue(false);
        MainApplication.getInstance().setService(null);
    }

    public boolean isServiceEnabled() {
        return isServiceConnected() && Boolean.TRUE.equals(serviceEnabled.getValue());
    }

    public boolean isServiceConnected() {
        return Boolean.TRUE.equals(serviceConnected.getValue());
    }

    public void setServiceEnabled(boolean enabled) {
        serviceEnabled.setValue(enabled);
        SettingSave.getInstance().setServiceEnabled(enabled);

        if (isServiceEnabled()) {
            for (Task task : TaskRepository.getInstance().getTasksByStart(TimeStartAction.class)) {
                for (StartAction startAction : task.getStartActions(TimeStartAction.class)) {
                    if (startAction.isEnable() && startAction.checkReady(null, task)) {
                        addWork(task, (TimeStartAction) startAction);
                    }
                }
            }
        } else {
            WorkManager.getInstance(this).cancelAllWork();
            stopAllTask();
        }
    }

    public void addRunningCallback(TaskRunningCallback callback) {
        callbacks.add(callback);
    }

    public void removeRunningCallback(TaskRunningCallback callback) {
        callbacks.remove(callback);
    }

    public void doOutAction(String actionId, HashMap<String, String> params) {
        if (!isServiceEnabled()) return;
        if (actionId != null) {
            for (Task task : TaskRepository.getInstance().getAllTasks()) {
                boolean flag = false;
                for (StartAction startAction : task.getStartActions(OutStartAction.class)) {
                    if (startAction.isEnable() && startAction.getId().equals(actionId)) {
                        flag = true;
                        Task copyTask = task.copy();
                        if (params != null) {
                            params.forEach((key, value) -> {
                                PinObject attr = copyTask.getAttr(key);
                                if (attr != null) attr.setParamValue(value);
                            });
                        }

                        if (copyTask.needCaptureService()) {
                            showToast(getString(R.string.capture_service_on_tips));
                            startCaptureService(result -> {
                                if (result) {
                                    runTask(copyTask, startAction);
                                }
                            });
                        } else {
                            runTask(copyTask, startAction);
                        }
                        break;
                    }
                }
                if (flag) break;
            }
        }
    }

    public void runTask(Task task, StartAction startAction) {
        runTask(task, startAction, null);
    }

    public TaskRunnable runTask(Task task, StartAction startAction, TaskRunningCallback callback) {
        return runTask(task, startAction, task, callback);
    }

    public TaskRunnable runTask(Task task, StartAction startAction, ActionContext actionContext, TaskRunningCallback callback) {
        if (actionContext == null || startAction == null) return null;
        if (!isServiceEnabled()) return null;

        // 放弃重入且有正在运行的任务
        if (startAction.getRestartType() == RestartType.CANCEL) {
            for (TaskRunnable runnable : runnableSet) {
                if (startAction.getId().equals(runnable.getStartAction().getId())) {
                    return null;
                }
            }
        }

        TaskRunnable runnable = new TaskRunnable(task, startAction, actionContext);
        if (callback != null) runnable.addCallback(callback);
        runnable.addCallback(new TaskRunningCallback() {
            @Override
            public void onStart(TaskRunnable runnable) {
                // 重新开始时需要停止之前的任务
                if (startAction.getRestartType() == RestartType.RESTART) {
                    stopTask(runnable.getStartTask());
                }

                runnableSet.add(runnable);
            }

            @Override
            public void onEnd(TaskRunnable runnable) {
                runnableSet.remove(runnable);
            }

            @Override
            public void onProgress(TaskRunnable runnable, int progress) {
            }

            @Override
            public void onAction(TaskRunnable runnable, ActionContext context, BaseAction action, int progress) {
            }
        });
        callbacks.stream().filter(Objects::nonNull).forEach(runnable::addCallback);

        Future<?> future = taskService.submit(runnable);
        runnable.setFuture(future);
        return runnable;
    }

    public void stopTask(Task task) {
        for (TaskRunnable taskRunnable : runnableSet) {
            if (task.getId().equals(taskRunnable.getStartTask().getId())) {
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

    public boolean isTaskRunning(Task task) {
        if (task == null) return false;
        for (TaskRunnable runnable : runnableSet) {
            if (task.getId().equals(runnable.getStartTask().getId())) {
                return true;
            }
        }
        return false;
    }

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

    public void startCaptureService(ResultCallback callback) {
        if (binder == null) {
            captureResultCallback = callback;
            Intent intent = new Intent(this, PermissionActivity.class);
            intent.putExtra(PermissionActivity.INTENT_KEY_START_CAPTURE, true);
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

    public boolean isCaptureEnabled() {
        return isServiceEnabled() && Boolean.TRUE.equals(captureEnabled.getValue());
    }

    public void replaceWork(Task task) {
        ArrayList<StartAction> startActions = task.getStartActions(TimeStartAction.class);

        Task originTask = TaskRepository.getInstance().getOriginTaskById(task.getId());
        WorkManager workManager = WorkManager.getInstance(this);
        if (originTask != null) {
            ArrayList<StartAction> originStartActions = originTask.getStartActions(TimeStartAction.class);

            // 在之前的任务中找已经不存在的动作并取消，存在的任务之后会被覆盖掉
            originStartActions.forEach(action -> {
                if (!action.isEnable()) return;

                boolean exist = false;
                for (StartAction startAction : startActions) {
                    if (startAction.getId().equals(action.getId())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    workManager.cancelUniqueWork(action.getId());
                }
            });
        }

        for (StartAction startAction : startActions) {
            if (startAction.isEnable() && startAction.checkReady(null, task)) {
                // 添加新的定时任务，覆盖之前设置的
                addWork(task, (TimeStartAction) startAction);
            } else {
                workManager.cancelUniqueWork(startAction.getId());
            }
        }
    }

    public void addWork(Task task, TimeStartAction startAction) {
        if (!isServiceEnabled()) return;
        if (task == null || startAction == null) return;
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
                            .putString(TaskWorker.TASK, task.getId())
                            .putString(TaskWorker.ACTION, startAction.getId())
                            .build())
                    .build();
            workManager.enqueueUniquePeriodicWork(startAction.getId(), ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, workRequest);

            TaskRepository.getInstance().addLog(task, startAction.getTitle(this),
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
                            .putString(TaskWorker.TASK, task.getId())
                            .putString(TaskWorker.ACTION, startAction.getId())
                            .build())
                    .build();
            workManager.enqueueUniqueWork(startAction.getId(), ExistingWorkPolicy.REPLACE, workRequest);

            TaskRepository.getInstance().addLog(task, startAction.getTitle(this),
                    getString(R.string.work_add,
                            getString(R.string.date,
                                    AppUtils.formatDateLocalDate(this, startTime),
                                    AppUtils.formatDateLocalTime(this, startTime))
                    )
            );
        }
    }

    public void runGesture(int x, int y, int time, ResultCallback callback) {
        if (x < 0 || y < 0) return;
        Path path = new Path();
        path.moveTo(x, y);
        runGesture(path, time, callback);
    }

    public void runGesture(Path path, int time, ResultCallback callback) {
        if (path == null) {
            if (callback != null) callback.onResult(false);
            return;
        }
        runGesture(Collections.singletonList(path), time, callback);
    }

    public void runGesture(List<Path> paths, int time, ResultCallback callback) {
        if (paths == null || paths.isEmpty()) {
            if (callback != null) callback.onResult(false);
            return;
        }
        GestureDescription.Builder builder = new GestureDescription.Builder();
        for (Path path : paths) {
            builder.addStroke(new GestureDescription.StrokeDescription(path, 0, time));
        }
        dispatchGesture(builder.build(), new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                if (callback != null) callback.onResult(true);
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                if (callback != null) callback.onResult(false);
            }
        }, null);
    }

    public void showToast(String msg) {
        MainActivity activity = MainApplication.getInstance().getActivity();
        if (activity == null) {
            Intent intent = new Intent(this, InstantActivity.class);
            intent.putExtra(InstantActivity.INTENT_KEY_SHOW_TOAST, msg);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            activity.showToast(msg);
        }
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
}
