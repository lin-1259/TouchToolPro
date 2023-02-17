package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.ui.app.AppView;

public class AppStartAction extends StartAction {
    private transient final Pin appPin;

    public AppStartAction(Context context) {
        super(context, R.string.action_app_start_title);
        appPin = addPin(new Pin(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE)));
    }

    public AppStartAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = addPin(tmpPins.remove(0));
    }

    @Override
    public boolean checkReady(ActionContext actionContext) {
        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getPackageName();
        if (packageName == null) return false;
        String activityName = worldState.getActivityName();

        MainAccessibilityService service = MainApplication.getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(actionContext, appPin);
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

        if (packages.containsKey(packageName)) {
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return false;

            // 活动为空表示只包含应用就行，或者包含对应活动才行
            return activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName());
        }
        return false;
    }
}
