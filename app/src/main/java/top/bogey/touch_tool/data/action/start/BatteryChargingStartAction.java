package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StartAction;
import top.bogey.touch_tool.data.action.state.BatteryChargingStateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class BatteryChargingStartAction extends StartAction {
    private transient final Pin<?> statePin;
    private transient int currState;

    public BatteryChargingStartAction(Context context) {
        super(context, R.string.action_battery_charging_start_title);
        statePin = addPin(new Pin<>(new PinSpinner(context.getResources().getStringArray(R.array.charging_state)), context.getString(R.string.action_battery_charging_start_subtitle_state)));
    }

    public BatteryChargingStartAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = addPin(tmpPins.remove(0));
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        int batteryState = worldState.getBatteryState();

        PinSpinner value = (PinSpinner) getPinValue(worldState, task, statePin);
        if (BatteryChargingStateAction.convertToChargingState(value.getIndex()) != batteryState)
            return false;

        // 当前已经是了，不再执行
        if (currState == batteryState) return false;
        currState = batteryState;
        return true;
    }
}
