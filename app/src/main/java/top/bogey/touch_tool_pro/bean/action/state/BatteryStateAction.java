package top.bogey.touch_tool_pro.bean.action.state;

import android.os.BatteryManager;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.WorldState;

public class BatteryStateAction extends Action {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.charging_state), R.string.action_battery_state_subtitle_state, true);
    private transient Pin valuePin = new Pin(new PinInteger(), R.string.action_battery_state_subtitle_battery, true);

    public BatteryStateAction() {
        super(ActionType.BATTERY_STATE);
        statePin = addPin(statePin);
        valuePin = addPin(valuePin);
    }

    public BatteryStateAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
        valuePin = reAddPin(valuePin);
    }

    public static int stateToIndex(int state) {
        return switch (state) {
            case BatteryManager.BATTERY_STATUS_CHARGING -> 0;
            case BatteryManager.BATTERY_STATUS_DISCHARGING -> 2;
            case BatteryManager.BATTERY_STATUS_FULL -> 3;
            default -> 1;
        };
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        int state = WorldState.getInstance().getBatteryState();
        int percent = WorldState.getInstance().getBatteryPercent();
        statePin.getValue(PinSpinner.class).setIndex(stateToIndex(state));
        valuePin.getValue(PinInteger.class).setValue(percent);
    }
}
