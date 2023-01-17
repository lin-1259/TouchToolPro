package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.ui.app.AppView;

public class AppStateAction extends StateAction {
    private final Pin<? extends PinObject> appPin;
    private final Pin<? extends PinObject> packagePin;
    private final Pin<? extends PinObject> activityPin;

    public AppStateAction() {
        super();
        appPin = addPin(new Pin<>(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE)));
        packagePin = addPin(new Pin<>(new PinString(), R.string.action_app_state_subtitle_package, PinDirection.OUT, PinSlotType.MULTI));
        activityPin = addPin(new Pin<>(new PinString(), R.string.action_app_state_subtitle_activity, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_app_state_title;
    }

    public AppStateAction(Parcel in) {
        super(in);
        appPin = addPin(pinsTmp.remove(0));
        packagePin = addPin(pinsTmp.remove(0));
        activityPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_app_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        value.setValue(false);
        PinString pkg = (PinString) packagePin.getValue();
        PinString act = (PinString) activityPin.getValue();

        CharSequence packageName = worldState.getPackageName();
        if (packageName != null) pkg.setValue(packageName.toString());

        CharSequence activityName = worldState.getActivityName();
        if (activityName != null) act.setValue(activityName.toString());

        if (!pin.getId().equals(statePin.getId())) return;

        if (packageName == null) return;

        MainAccessibilityService service = MainApplication.getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(worldState, task, appPin);
        Map<CharSequence, ArrayList<CharSequence>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName)) return;

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) {
            value.setValue(true);
            return;
        }

        if (packages.containsKey(packageName)) {
            ArrayList<CharSequence> activityClasses = packages.get(packageName);
            if (activityClasses == null) return;

            value.setValue(activityClasses.isEmpty() || activityClasses.contains(activityName));
        }
    }
}
