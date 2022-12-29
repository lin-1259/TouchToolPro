package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinBoolean;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSelectApp;
import top.bogey.touch_tool.ui.app.AppView;

public class AppStateAction extends StateAction {
    private final Pin<? extends PinObject> appPin;

    public AppStateAction() {
        super();
        appPin = addPin(new Pin<>(new PinSelectApp(AppView.MULTI_WITH_ACTIVITY_MODE)));
        titleId = R.string.action_app_state_title;
    }

    public AppStateAction(Parcel in) {
        super(in);
        appPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_app_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        CharSequence packageName = worldState.getPackageName();
        if (packageName != null) {
            PinSelectApp helper = (PinSelectApp) appPin.getValue();
            Map<CharSequence, ArrayList<CharSequence>> packages = helper.getPackages();
            ArrayList<CharSequence> activityClasses = packages.get(packageName);
            if (activityClasses != null) {
                value.setValue(activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName()));
            }
        }
        value.setValue(false);
    }
}
