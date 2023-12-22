package top.bogey.touch_tool_pro.service;

import static top.bogey.touch_tool_pro.ui.InstantActivity.ACTION_ID;
import static top.bogey.touch_tool_pro.ui.InstantActivity.TASK_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.InnerStartAction;
import top.bogey.touch_tool_pro.bean.action.start.TimeStartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.WorldState;

public class SystemEventReceiver extends BroadcastReceiver {
    public final static String ALARM_TASK = "top.bogey.touch_tool_pro.alarm_task";

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (intentAction == null) return;
        switch (intentAction) {
            // 电量变更
            case Intent.ACTION_BATTERY_CHANGED -> {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int state = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                WorldState.getInstance().setBatteryState(level * 100 / scale, state);
            }

            // 这个是为了停止任务做的
            case Intent.ACTION_SCREEN_OFF, Intent.ACTION_SCREEN_ON, Intent.ACTION_USER_PRESENT, Intent.ACTION_TIME_TICK -> WorldState.getInstance().checkAutoStartAction(InnerStartAction.class);

            // 更新应用列表
            case Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_CHANGED -> {
                String packageName = intent.getDataString();
                if (packageName == null) return;
                WorldState.getInstance().resetAppMap(context);
            }

            // 执行定时任务
            case ALARM_TASK -> {
                String taskId = intent.getStringExtra(TASK_ID);
                String actionId = intent.getStringExtra(ACTION_ID);

                if (taskId != null && actionId != null) {
                    Task task = SaveRepository.getInstance().getTaskById(taskId);
                    if (task != null) {
                        Action action = task.getActionById(actionId);
                        MainAccessibilityService service = MainApplication.getInstance().getService();
                        if (service != null && service.isServiceEnabled()) {
                            if (action instanceof TimeStartAction timeStartAction) {
                                service.runTask(task, timeStartAction);
                                if (timeStartAction.getPeriodic() > 0) {
                                    service.addAlarm(task, timeStartAction);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 这些过滤是为了停止任务
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_TIME_TICK);
        // 过滤应用安装卸载更新
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        // 过滤定时执行
        filter.addAction(ALARM_TASK);
        return filter;
    }
}
