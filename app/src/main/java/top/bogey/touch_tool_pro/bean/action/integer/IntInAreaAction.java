package top.bogey.touch_tool_pro.bean.action.integer;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.check.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntInAreaAction extends CheckAction {
    protected transient Pin areaPin = new Pin(new PinValueArea(1, 100, 1), R.string.pin_value_area);
    protected transient Pin valuePin = new Pin(new PinInteger(), R.string.pin_int);

    public IntInAreaAction() {
        super(ActionType.INT_IN_AREA);
        areaPin = addPin(areaPin);
        valuePin = addPin(valuePin);
    }

    public IntInAreaAction(JsonObject jsonObject) {
        super(jsonObject);
        areaPin = reAddPin(areaPin);
        valuePin = reAddPin(valuePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinValueArea area = (PinValueArea) getPinValue(runnable, context, areaPin);
        PinInteger value = (PinInteger) getPinValue(runnable, context, valuePin);

        result.setBool(value.getValue() <= area.getLow() && value.getValue() >= area.getHigh());
    }
}
