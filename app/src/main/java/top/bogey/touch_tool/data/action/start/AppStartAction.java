package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.ui.app.AppView;

public class AppStartAction extends StartAction {
    private final Pin<? extends PinObject> appPin;

    public AppStartAction() {
        super();
        appPin = addPin(new Pin<>(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE)));
        titleId = R.string.action_app_start_title;
    }

    public AppStartAction(Parcel in) {
        super(in);
        appPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_app_start_title;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        CharSequence packageName = worldState.getPackageName();
        if (packageName == null) return false;

        MainAccessibilityService service = MainApplication.getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(worldState, task, appPin);
        Map<CharSequence, ArrayList<CharSequence>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName)) {
            return false;
        }

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) {
            return true;
        }

        if (packages.containsKey(packageName)) {
            ArrayList<CharSequence> activityClasses = packages.get(packageName);
            if (activityClasses == null) return false;

            return activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName());
        }
        return false;
    }
}
