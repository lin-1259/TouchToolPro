package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntEqualAction extends IntCheckAction {
    public IntEqualAction() {
        super(ActionType.INT_EQUAL);
    }

    public IntEqualAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinInteger first = (PinInteger) getPinValue(runnable, context, firstPin);
        PinInteger second = (PinInteger) getPinValue(runnable, context, secondPin);

        result.setBool(Objects.equals(first.getValue(), second.getValue()));
    }
}
