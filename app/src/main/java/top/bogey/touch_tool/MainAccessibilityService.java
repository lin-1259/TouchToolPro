package top.bogey.touch_tool;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.Notification;
import android.content.Intent;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.data.receiver.BatteryReceiver;
import top.bogey.touch_tool.ui.setting.SettingSave;
import top.bogey.touch_tool.utils.ResultCallback;
import top.bogey.touch_tool.utils.TaskQueue;
import top.bogey.touch_tool.utils.TaskThreadPoolExecutor;

public class MainAccessibilityService extends AccessibilityService {
    private BatteryReceiver batteryReceiver;

    // 服务
    private boolean serviceConnected = false;
    public static final MutableLiveData<Boolean> serviceEnabled = new MutableLiveData<>(false);

    public final ExecutorService taskService = new TaskThreadPoolExecutor(3, 30, 30L, TimeUnit.SECONDS, new TaskQueue<>(20));
    private final HashSet<TaskRunnable> runnables = new HashSet<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event != null) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                WorldState.getInstance().enterActivity(event.getPackageName(), event.getClassName());
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

        serviceConnected = false;
        MainApplication.setService(null);
    }

    public boolean isServiceEnabled() {
        return serviceConnected && Boolean.TRUE.equals(serviceEnabled.getValue());
    }

    public void setServiceEnabled(boolean enabled) {
        serviceEnabled.setValue(enabled);
        SettingSave.getInstance().setServiceEnabled(enabled);
    }




    public TaskRunnable runTask(Task task, StartAction startAction) {
        if (task == null || startAction == null) return null;
        if (!isServiceEnabled()) return null;

        TaskRunnable runnable = new TaskRunnable(task, startAction);
        taskService.submit(runnable);
        runnables.add(runnable);
        return runnable;
    }

    public void stopTask(TaskRunnable runnable) {

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
