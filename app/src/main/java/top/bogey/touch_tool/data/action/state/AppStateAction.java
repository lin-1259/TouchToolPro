package top.bogey.touch_tool.data.action.state;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.ui.app.AppView;

public class AppStateAction extends StateAction {
    private transient final Pin<?> appPin;
    private transient final Pin<?> packagePin;
    private transient final Pin<?> activityPin;

    public AppStateAction(Context context) {
        super(context, R.string.action_app_state_title);
        appPin = addPin(new Pin<>(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE)));
        packagePin = addPin(new Pin<>(new PinString(), context.getString(R.string.action_app_state_subtitle_package), PinDirection.OUT, PinSlotType.MULTI));
        activityPin = addPin(new Pin<>(new PinString(), context.getString(R.string.action_app_state_subtitle_activity), PinDirection.OUT, PinSlotType.MULTI));
    }

    public AppStateAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = addPin(tmpPins.remove(0));
        packagePin = addPin(tmpPins.remove(0));
        activityPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<?> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        value.setValue(false);
        PinString pkg = (PinString) packagePin.getValue();
        PinString act = (PinString) activityPin.getValue();

        String packageName = worldState.getPackageName();
        if (packageName != null) pkg.setValue(packageName.toString());

        String activityName = worldState.getActivityName();
        if (activityName != null) act.setValue(activityName.toString());

        if (!pin.getId().equals(statePin.getId())) return;

        if (packageName == null) return;

        MainAccessibilityService service = MainApplication.getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(worldState, task, appPin);
        Map<String, ArrayList<String>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName)) return;

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) {
            value.setValue(true);
            return;
        }

        if (packages.containsKey(packageName)) {
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return;

            value.setValue(activityClasses.isEmpty() || activityClasses.contains(activityName));
        }
    }
}
