package top.bogey.touch_tool.data.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.state.BatteryChargingStateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class BatteryChargingStartAction extends StartAction {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.charging_state), R.string.action_battery_charging_start_subtitle_state);
    private transient int currState;

    public BatteryChargingStartAction() {
        super(R.string.action_battery_charging_start_title);
        statePin = addPin(statePin);
    }

    public BatteryChargingStartAction(JsonObject jsonObject) {
        super(R.string.action_battery_charging_start_title, jsonObject);
        statePin = reAddPin(statePin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        WorldState worldState = WorldState.getInstance();
        int batteryState = worldState.getBatteryState();

        PinSpinner value = (PinSpinner) getPinValue(runnable, actionContext, statePin);
        if (BatteryChargingStateAction.convertToChargingState(value.getIndex()) != batteryState)
            return false;

        // 当前已经是了，不再执行
        if (currState == batteryState) return false;
        currState = batteryState;
        return true;
    }
}
