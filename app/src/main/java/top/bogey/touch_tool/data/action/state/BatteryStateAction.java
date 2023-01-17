package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class BatteryStateAction extends StateAction {
    private final Pin<? extends PinObject> areaPin;

    public BatteryStateAction() {
        super();
        areaPin = addPin(new Pin<>(new PinValueArea(1, 100, 1), R.string.action_battery_state_subtitle_battery));
        titleId = R.string.action_battery_state_title;
    }

    public BatteryStateAction(Parcel in) {
        super(in);
        areaPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_battery_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        int batteryPercent = worldState.getBatteryPercent();
        PinValueArea valueArea = (PinValueArea) getPinValue(worldState, task, areaPin);
        int low = valueArea.getCurrMin();
        int high = valueArea.getCurrMax();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        value.setValue(result);
    }
}
