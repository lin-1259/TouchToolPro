package top.bogey.touch_tool.data.action.state;

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

public class AppStateAction extends StateAction {
    private transient Pin appPin = new Pin(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE));

    public AppStateAction() {
        super(R.string.action_app_state_title);
        appPin = addPin(appPin);
    }

    public AppStateAction(JsonObject jsonObject) {
        super(R.string.action_app_state_title, jsonObject);
        appPin = reAddPin(appPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        value.setValue(false);

        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getPackageName();
        if (packageName == null) return;
        String activityName = worldState.getActivityName();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(runnable, actionContext, appPin);
        Map<String, ArrayList<String>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包内的一些东西
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName)) {
            // 看下是排除活动还是排除应用
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return;
            // 活动为空或者活动包含在排除中，返回
            if (activityClasses.isEmpty() || activityClasses.contains(activityName)) return;
        }

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) {
            value.setValue(true);
            return;
        }

        if (packages.containsKey(packageName)) {
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return;

            // 活动为空表示只包含应用就行，或者包含对应活动才行
            value.setValue(activityClasses.isEmpty() || activityClasses.contains(activityName));
        }
    }
}
