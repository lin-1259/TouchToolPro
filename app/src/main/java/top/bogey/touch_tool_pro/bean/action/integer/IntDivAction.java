package top.bogey.touch_tool_pro.bean.action.integer;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntDivAction extends IntAction {

    public IntDivAction() {
        super(ActionType.INT_DIV);
    }

    public IntDivAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger result = resultPin.getValue(PinInteger.class);

        PinInteger first = (PinInteger) getPinValue(runnable, context, firstPin);
        PinInteger second = (PinInteger) getPinValue(runnable, context, secondPin);

        if (second.getValue() == 0) {
            result.setValue(first.getValue());
        } else {
            result.setValue(first.getValue() / second.getValue());
        }
    }
}
