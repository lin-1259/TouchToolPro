package top.bogey.touch_tool.data.action.start;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.ui.app.AppView;

public class AppStartAction extends StartAction {
    private transient Pin appPin = new Pin(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE));
    private transient Pin breakPin = new Pin(new PinBoolean(true), R.string.action_app_start_subtitle_break);

    public AppStartAction() {
        super(R.string.action_app_start_title);
        appPin = addPin(appPin);
        breakPin = addPin(breakPin);
    }

    public AppStartAction(JsonObject jsonObject) {
        super(R.string.action_app_start_title, jsonObject);
        appPin = reAddPin(appPin);
        breakPin = reAddPin(breakPin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getPackageName();
        if (packageName == null) return false;
        String activityName = worldState.getActivityName();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(runnable, actionContext, appPin);
        Map<String, ArrayList<String>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包内的一些东西
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName)) {
            // 看下是排除活动还是排除应用
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return false;
            // 活动为空或者活动包含在排除中，返回
            if (activityClasses.isEmpty() || activityClasses.contains(activityName)) return false;
        }

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) {
            return true;
        }

        // 包含自己
        if (packages.containsKey(packageName)) {
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return false;

            // 活动为空表示只包含应用就行，或者包含对应活动才行
            return activityClasses.isEmpty() || activityClasses.contains(activityName);
        }
        return false;
    }

    @Override
    public boolean checkStop(TaskRunnable runnable, ActionContext actionContext) {
        PinBoolean needBreak = (PinBoolean) getPinValue(runnable, actionContext, breakPin);
        if (needBreak.getValue()) {
            return !checkReady(runnable, actionContext);
        }
        return false;
    }
}
