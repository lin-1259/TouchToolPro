package top.bogey.touch_tool;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.data.receiver.BatteryReceiver;
import top.bogey.touch_tool.ui.setting.SettingSave;
import top.bogey.touch_tool.utils.ResultCallback;
import top.bogey.touch_tool.utils.TaskQueue;
import top.bogey.touch_tool.utils.TaskRunningCallback;
import top.bogey.touch_tool.utils.TaskThreadPoolExecutor;

public class MainAccessibilityService extends AccessibilityService {
    private BatteryReceiver batteryReceiver;

    // 服务
    private boolean serviceConnected = false;
    public static final MutableLiveData<Boolean> serviceEnabled = new MutableLiveData<>(false);

    // 截屏
    public static final MutableLiveData<Boolean> captureEnabled = new MutableLiveData<>(false);
    public MainCaptureService.CaptureServiceBinder binder = null;
    private ServiceConnection connection = null;
    private ResultCallback captureResultCallback;

    public final ExecutorService taskService = new TaskThreadPoolExecutor(3, 30, 30L, TimeUnit.SECONDS, new TaskQueue<>(20));
    private final HashSet<TaskRunnable> runnables = new HashSet<>();
    private final HashSet<TaskRunningCallback> callbacks = new HashSet<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                WorldState.getInstance().enterActivity(event.getPackageName(), event.getClassName());
                if (getPackageName().contentEquals(event.getPackageName())) stopAllTask();
            } else if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                if (!Notification.class.getName().contentEquals(event.getClassName())) return;
                List<CharSequence> eventText = event.getText();
                if (eventText == null || eventText.size() == 0) return;
                StringBuilder builder = new StringBuilder();
                for (CharSequence charSequence : eventText) {
                    builder.append(charSequence);
                    builder.append(" ");
                }
                WorldState.getInstance().setNotificationText(builder.toString().trim());
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        serviceConnected = true;
        setServiceEnabled(SettingSave.getInstance().isServiceEnabled());
        MainApplication.setService(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceConnected = false;
        MainApplication.setService(null);

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MainApplication.setService(this);
        if (intent != null) {
            boolean startCaptureService = intent.getBooleanExtra(MainActivity.INTENT_KEY_START_CAPTURE, false);
            boolean isBackground = intent.getBooleanExtra(MainActivity.INTENT_KEY_BACKGROUND, false);
            if (startCaptureService) startCaptureService(isBackground, captureResultCallback);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, batteryReceiver.getFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (batteryReceiver != null) unregisterReceiver(batteryReceiver);
        stopCaptureService();

        serviceConnected = false;
        MainApplication.setService(null);
    }

    public boolean isServiceEnabled() {
        return serviceConnected && Boolean.TRUE.equals(serviceEnabled.getValue());
    }

    public boolean isServiceConnected() {
        return serviceConnected;
    }

    public void setServiceEnabled(boolean enabled) {
        serviceEnabled.setValue(enabled);
        SettingSave.getInstance().setServiceEnabled(enabled);
    }

    public void addCallback(TaskRunningCallback callback) {
        callbacks.add(callback);
        runnables.forEach(runnable -> runnable.addCallback(callback));
    }

    public void removeCallback(TaskRunningCallback callback) {
        callbacks.remove(callback);
        runnables.forEach(runnable -> runnable.removeCallback(callback));
    }

    public TaskRunnable runTask(Task task, StartAction startAction) {
        if (task == null || startAction == null) return null;
        if (!isServiceEnabled()) return null;

        if (!stopTaskIfNeed(task, startAction)) return null;

        TaskRunnable runnable = new TaskRunnable(task, startAction);
        runnable.addCallback(new TaskRunningCallback() {
            @Override
            public void onStart(TaskRunnable runnable) {
                synchronized (runnables) {
                    runnables.add(runnable);
                }
            }

            @Override
            public void onEnd(TaskRunnable runnable) {
                synchronized (runnables) {
                    runnables.remove(runnable);
                }
            }

            @Override
            public void onProgress(TaskRunnable runnable, int progress) {

            }
        });

        callbacks.stream().filter(Objects::nonNull).forEach(runnable::addCallback);

        Future<?> future = taskService.submit(runnable);
        runnable.setFuture(future);
        return runnable;
    }

    public void stopTask(TaskRunnable runnable) {
        if (runnables.contains(runnable)) runnable.stop();
    }

    public void stopAllTask() {
        synchronized (runnables) {
            for (TaskRunnable taskRunnable : runnables) {
                taskRunnable.stop();
            }
            runnables.clear();
        }
    }

    public boolean stopTaskIfNeed(Task task, StartAction startAction) {
        if (task == null || startAction == null) return false;
        synchronized (runnables) {
            switch (startAction.getRestartType()) {
                // 需要重新运行
                case RESTART:
                    for (TaskRunnable runnable : runnables) {
                        if (task.getId().equals(runnable.getTask().getId()) && startAction.getId().equals(runnable.getStartAction().getId())) {
                            stopTask(runnable);
                        }
                    }
                    return true;
                // 如果没有运行，则运行；如果正在运行，取消本次运行
                case CANCEL:
                    boolean flag = true;
                    for (TaskRunnable runnable : runnables) {
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
                activity.launchNotification((notiCode, notiIntent) -> {
                    if (notiCode == Activity.RESULT_OK) {
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
                                intent.putExtra("Data", data);
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
}
