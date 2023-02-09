package top.bogey.touch_tool;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Path;
import android.os.IBinder;
import android.view.accessibility.AccessibilityEvent;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.TaskWorker;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StartAction;
import top.bogey.touch_tool.data.action.start.OutStartAction;
import top.bogey.touch_tool.data.action.start.TimeStartAction;
import top.bogey.touch_tool.data.receiver.BatteryReceiver;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.utils.ResultCallback;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.TaskQueue;
import top.bogey.touch_tool.utils.TaskRunningCallback;
import top.bogey.touch_tool.utils.TaskThreadPoolExecutor;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

public class MainAccessibilityService extends AccessibilityService {
    private BatteryReceiver batteryReceiver;

    // 服务
    public static final MutableLiveData<Boolean> serviceConnected = new MutableLiveData<>(false);
    public static final MutableLiveData<Boolean> serviceEnabled = new MutableLiveData<>(false);

    // 截屏
    public static final MutableLiveData<Boolean> captureEnabled = new MutableLiveData<>(false);
    public MainCaptureService.CaptureServiceBinder binder = null;
    private ServiceConnection connection = null;
    private ResultCallback captureResultCallback;

    public final ExecutorService taskService = new TaskThreadPoolExecutor(3, 30, 30L, TimeUnit.SECONDS, new TaskQueue<>(20));
    private final HashSet<TaskRunnable> runnableSet = new HashSet<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null) {
            String packageName = (String) event.getPackageName();
            String className = (String) event.getClassName();
            if (packageName == null || className == null) return;

            WorldState worldState = WorldState.getInstance();
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (getPackageName().contentEquals(packageName) && worldState.isActivityClass(packageName, className)) {
                    stopAllTask();
                    worldState.setEnterActivity(packageName, className);
                } else worldState.enterActivity(packageName, className);

            } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                if (!Notification.class.getName().contentEquals(className)) return;
                List<CharSequence> eventText = event.getText();
                if (eventText == null || eventText.size() == 0) return;
                StringBuilder builder = new StringBuilder();
                for (CharSequence charSequence : eventText) {
                    builder.append(charSequence);
                    builder.append(" ");
                }
                worldState.setNotification(packageName, builder.toString().trim());
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        serviceConnected.setValue(true);
        serviceEnabled.setValue(SettingSave.getInstance().isServiceEnabled());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceConnected.setValue(false);
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean startCaptureService = intent.getBooleanExtra(MainActivity.INTENT_KEY_START_CAPTURE, false);
            boolean isBackground = intent.getBooleanExtra(MainActivity.INTENT_KEY_BACKGROUND, false);
            if (startCaptureService) startCaptureService(isBackground, captureResultCallback);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        MainApplication.setService(this);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, batteryReceiver.getFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) unregisterReceiver(batteryReceiver);
        stopCaptureService();

        serviceConnected.setValue(false);
        serviceEnabled.setValue(false);
        MainApplication.setService(null);
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
                    if (startAction.isEnable() && startAction.checkReady(WorldState.getInstance(), task)) {
                        addWork(task, (TimeStartAction) startAction);
                    }
                }
            }
        } else {
            WorkManager.getInstance(this).cancelAllWork();
        }
    }

    public void doOutAction(String actionId) {
        if (actionId != null) {
            MainAccessibilityService service = MainApplication.getService();
            if (service != null && service.isServiceEnabled()) {
                for (Task task : TaskRepository.getInstance().getAllTasks()) {
                    boolean flag = false;
                    for (StartAction startAction : task.getStartActions(OutStartAction.class)) {
                        if (startAction.isEnable() && startAction.getId().equals(actionId)) {
                            flag = true;
                            if (task.needCaptureService()) {
                                service.showToast(service.getString(R.string.capture_service_on_tips));
                                service.startCaptureService(true, result -> {
                                    if (result) service.runTask(task, startAction);
                                });
                            } else {
                                service.runTask(task, startAction);
                            }
                            break;
                        }
                    }
                    if (flag) break;
                }
            }
        }
    }

    public TaskRunnable runTask(Task task, StartAction startAction) {
        if (task == null || startAction == null) return null;
        if (!isServiceEnabled()) return null;

        if (!stopTaskIfNeed(task, startAction)) return null;

        KeepAliveFloatView view = (KeepAliveFloatView) EasyFloat.getView(KeepAliveFloatView.class.getCanonicalName());
        if (view != null) {
            view.showMe();
        }

        TaskRunnable runnable = new TaskRunnable(task, startAction);
        runnable.addCallback(new TaskRunningCallback() {
            @Override
            public void onStart(TaskRunnable runnable) {
                synchronized (runnableSet) {
                    runnableSet.add(runnable);
                }
            }

            @Override
            public void onEnd(TaskRunnable runnable) {
                synchronized (runnableSet) {
                    runnableSet.remove(runnable);
                }
            }

            @Override
            public void onProgress(TaskRunnable runnable, int progress) {

            }
        });

        Future<?> future = taskService.submit(runnable);
        runnable.setFuture(future);
        return runnable;
    }

    public void stopTask(TaskRunnable runnable) {
        if (runnableSet.contains(runnable)) runnable.stop();
    }

    public void stopAllTask() {
        synchronized (runnableSet) {
            for (TaskRunnable taskRunnable : runnableSet) {
                taskRunnable.stop();
            }
            runnableSet.clear();
        }
    }

    public boolean stopTaskIfNeed(Task task, StartAction startAction) {
        if (task == null || startAction == null) return false;
        synchronized (runnableSet) {
            switch (startAction.getRestartType()) {
                // 需要重新运行
                case RESTART:
                    for (TaskRunnable runnable : runnableSet) {
                        if (task.getId().equals(runnable.getTask().getId()) && startAction.getId().equals(runnable.getStartAction().getId())) {
                            stopTask(runnable);
                        }
                    }
                    return true;
                // 如果没有运行，则运行；如果正在运行，取消本次运行
                case CANCEL:
                    boolean flag = true;
                    for (TaskRunnable runnable : runnableSet) {
                        if (task.getId().equals(runnable.getTask().getId()) && startAction.getId().equals(runnable.getStartAction().getId())) {
                            flag = false;
                            break;
                        }
                    }
                    return flag;
                // 每次都运行新的
                case START_NEW:
                    return true;
            }
        }
        return false;
    }

    public void startCaptureService(boolean moveBack, ResultCallback callback) {
        if (binder == null) {
            MainActivity activity = MainApplication.getActivity();
            if (activity != null) {
                activity.launchNotification((notifyCode, notifyIntent) -> {
                    if (notifyCode == Activity.RESULT_OK) {
                        activity.launchCapture(((code, data) -> {
                            if (code == Activity.RESULT_OK) {
                                connection = new ServiceConnection() {
                                    @Override
                                    public void onServiceConnected(ComponentName name, IBinder service) {
                                        binder = (MainCaptureService.CaptureServiceBinder) service;
                                        captureEnabled.setValue(true);
                                        if (moveBack) activity.moveTaskToBack(true);
                                        if (callback != null) callback.onResult(true);
                                    }

                                    @Override
                                    public void onServiceDisconnected(ComponentName name) {
                                        stopCaptureService();
                                    }
                                };
                                Intent intent = new Intent(this, MainCaptureService.class);
                                intent.putExtra(MainCaptureService.DATA, data);
                                boolean result = bindService(intent, connection, Context.BIND_AUTO_CREATE);
                                if (!result) if (callback != null) callback.onResult(false);
                            } else {
                                if (callback != null) callback.onResult(false);
                            }
                        }));
                    } else {
                        if (callback != null) callback.onResult(false);
                    }
                });
            } else {
                captureResultCallback = callback;
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(MainActivity.INTENT_KEY_BACKGROUND, true);
                intent.putExtra(MainActivity.INTENT_KEY_START_CAPTURE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
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
        captureEnabled.setValue(false);
    }

    public boolean isCaptureEnabled() {
        return isServiceEnabled() && Boolean.TRUE.equals(captureEnabled.getValue());
    }

    public void replaceWork(Task task) {
        ArrayList<StartAction> startActions = task.getStartActions(TimeStartAction.class);

        Task originTask = TaskRepository.getInstance().getOriginTaskById(task.getId());
        if (originTask != null) {
            WorkManager workManager = WorkManager.getInstance(this);
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

        // 添加新的定时任务，覆盖之前设置的
        for (StartAction startAction : startActions) {
            if (startAction.isEnable() && startAction.checkReady(WorldState.getInstance(), task)) {
                addWork(task, (TimeStartAction) startAction);
            }
        }
    }

    public void addWork(Task task, TimeStartAction startAction) {
        if (!isServiceEnabled()) return;
        if (task == null || startAction == null) return;
        WorkManager workManager = WorkManager.getInstance(this);
        WorldState worldState = WorldState.getInstance();

        long timeMillis = System.currentTimeMillis();

        long startTime = startAction.getStartTime(worldState, task);
        long periodic = startAction.getPeriodic(worldState, task);

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
        } else {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskWorker.class)
                    .setInitialDelay(startTime - timeMillis, TimeUnit.MILLISECONDS)
                    .setInputData(new Data.Builder()
                            .putString(TaskWorker.TASK, task.getId())
                            .putString(TaskWorker.ACTION, startAction.getId())
                            .build())
                    .build();
            workManager.enqueueUniqueWork(startAction.getId(), ExistingWorkPolicy.REPLACE, workRequest);
        }
    }

    public void runGesture(int x, int y, int time, ResultCallback callback) {
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
        MainActivity activity = MainApplication.getActivity();
        if (activity == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.INTENT_KEY_BACKGROUND, true);
            intent.putExtra(MainActivity.INTENT_KEY_SHOW_TOAST, msg);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            activity.showToast(msg);
        }
    }
}
