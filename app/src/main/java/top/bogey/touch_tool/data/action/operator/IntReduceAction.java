package top.bogey.touch_tool.data.action.operator;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntReduceAction extends IntDivAction {

    public IntReduceAction() {
        super(R.string.action_int_reduce_operator_title);
    }

    public IntReduceAction(JsonObject jsonObject) {
        super(R.string.action_int_reduce_operator_title, jsonObject);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) outValuePin.getValue();
        value.setValue(0);

        PinInteger origin = (PinInteger) getPinValue(runnable, actionContext, originPin);
        PinInteger second = (PinInteger) getPinValue(runnable, actionContext, secondPin);
        value.setValue(origin.getValue() - second.getValue());
    }
}
