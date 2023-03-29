package top.bogey.touch_tool.data.action.state;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class BatteryStateAction extends StateAction {
    private transient Pin areaPin = new Pin(new PinValueArea(1, 100, 1), R.string.action_battery_state_subtitle_battery);

    public BatteryStateAction() {
        super(R.string.action_battery_state_title);
        areaPin = addPin(areaPin);
    }

    public BatteryStateAction(JsonObject jsonObject) {
        super(R.string.action_battery_state_title, jsonObject);
        areaPin = reAddPin(areaPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        WorldState worldState = WorldState.getInstance();
        int batteryPercent = worldState.getBatteryPercent();
        PinValueArea valueArea = (PinValueArea) getPinValue(runnable, actionContext, areaPin);
        int low = valueArea.getCurrMin();
        int high = valueArea.getCurrMax();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        value.setValue(result);
    }
}
