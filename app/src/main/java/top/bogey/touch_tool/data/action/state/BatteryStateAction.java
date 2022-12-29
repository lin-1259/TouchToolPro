package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinBoolean;
import top.bogey.touch_tool.data.action.pin.object.PinInteger;
import top.bogey.touch_tool.data.action.pin.object.PinObject;

public class BatteryStateAction extends StateAction {
    private final Pin<? extends PinObject> lowPin;
    private final Pin<? extends PinObject> highPin;

    public BatteryStateAction() {
        super();
        lowPin = addPin(new Pin<>(new PinInteger(1), R.string.action_battery_state_subtitle_low));
        highPin = addPin(new Pin<>(new PinInteger(100), R.string.action_battery_state_subtitle_high));
        titleId = R.string.action_battery_state_title;
    }

    public BatteryStateAction(Parcel in) {
        super(in);
        lowPin = addPin(pinsTmp.remove(0));
        highPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_battery_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        int batteryPercent = worldState.getBatteryPercent();
        int low = ((PinInteger) lowPin.getValue()).getValue();
        int high = ((PinInteger) highPin.getValue()).getValue();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        value.setValue(result);
    }
}
