package top.bogey.touch_tool_pro.bean.action.integer;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IntAddAction extends IntMoreAction {
    public IntAddAction() {
        super(ActionType.INT_ADD);
    }

    public IntAddAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger result = resultPin.getValue(PinInteger.class);

        int total = 0;
        for (Pin valuePin : calculateMorePins()) {
            PinInteger value = (PinInteger) getPinValue(runnable, context, valuePin);
            total += value.getValue();
        }

        result.setValue(total);
    }
}
