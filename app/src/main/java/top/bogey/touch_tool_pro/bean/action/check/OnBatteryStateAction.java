package top.bogey.touch_tool_pro.bean.action.check;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class OnBatteryStateAction extends CheckAction {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.charging_state), R.string.action_battery_state_check_subtitle_state);
    private transient Pin checkStatePin = new Pin(new PinSpinner(R.array.charging_state), R.string.action_battery_state_check_subtitle_check_state);

    public OnBatteryStateAction() {
        super(ActionType.CHECK_ON_BATTERY_STATE);
        statePin = addPin(statePin);
        checkStatePin = addPin(checkStatePin);
    }

    public OnBatteryStateAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
        checkStatePin = reAddPin(checkStatePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinSpinner state = (PinSpinner) getPinValue(runnable, context, statePin);
        PinSpinner checkState = (PinSpinner) getPinValue(runnable, context, checkStatePin);
        result.setBool(state.getIndex() == checkState.getIndex());
    }
}
