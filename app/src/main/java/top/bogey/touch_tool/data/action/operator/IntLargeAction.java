package top.bogey.touch_tool.data.action.operator;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntLargeAction extends IntEqualAction {

    public IntLargeAction() {
        super(R.string.action_int_large_operator_title);
    }

    public IntLargeAction(JsonObject jsonObject) {
        super(R.string.action_int_large_operator_title, jsonObject);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinBoolean value = (PinBoolean) outValuePin.getValue();
        value.setValue(false);

        PinInteger origin = (PinInteger) getPinValue(runnable, actionContext, originPin);
        PinInteger second = (PinInteger) getPinValue(runnable, actionContext, secondPin);
        value.setValue(origin.getValue() > second.getValue());
    }
}
