package top.bogey.touch_tool_pro.bean.action.integer;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntRandomAction extends Action {
    protected transient Pin valuePin = new Pin(new PinInteger(), R.string.pin_int, true);
    protected transient Pin areaPin = new Pin(new PinValueArea(1, 100, 1), R.string.pin_value_area);

    public IntRandomAction() {
        super(ActionType.INT_RANDOM);
        valuePin = addPin(valuePin);
        areaPin = addPin(areaPin);
    }

    public IntRandomAction(JsonObject jsonObject) {
        super(jsonObject);
        valuePin = reAddPin(valuePin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger value = valuePin.getValue(PinInteger.class);

        PinValueArea area = (PinValueArea) getPinValue(runnable, context, areaPin);
        value.setValue(area.getRandom());
    }
}
