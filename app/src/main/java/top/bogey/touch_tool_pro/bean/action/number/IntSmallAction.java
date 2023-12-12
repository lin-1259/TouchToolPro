package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntSmallAction extends IntCheckAction {
    public IntSmallAction() {
        super(ActionType.INT_SMALL);
    }

    public IntSmallAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinInteger first = (PinInteger) getPinValue(runnable, context, firstPin);
        PinInteger second = (PinInteger) getPinValue(runnable, context, secondPin);

        result.setBool(first.getValue() < second.getValue());
    }
}
