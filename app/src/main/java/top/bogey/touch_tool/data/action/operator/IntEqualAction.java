package top.bogey.touch_tool.data.action.operator;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntEqualAction extends CalculateAction {
    protected transient Pin outValuePin = new Pin(new PinBoolean(), PinDirection.OUT);
    protected transient Pin originPin = new Pin(new PinInteger());
    protected transient Pin secondPin = new Pin(new PinInteger());

    public IntEqualAction() {
        this(R.string.action_int_equal_operator_title);
    }

    public IntEqualAction(@StringRes int titleId) {
        super(titleId);
        outValuePin = addPin(outValuePin);
        originPin = addPin(originPin);
        secondPin = addPin(secondPin);
    }

    public IntEqualAction(JsonObject jsonObject) {
        this(R.string.action_int_equal_operator_title, jsonObject);
    }

    public IntEqualAction(@StringRes int titleId, JsonObject jsonObject) {
        super(titleId, jsonObject);
        outValuePin = reAddPin(outValuePin);
        originPin = reAddPin(originPin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinBoolean value = (PinBoolean) outValuePin.getValue();
        value.setValue(false);

        PinInteger origin = (PinInteger) getPinValue(runnable, actionContext, originPin);
        PinInteger second = (PinInteger) getPinValue(runnable, actionContext, secondPin);
        value.setValue(origin.getValue() == second.getValue());
    }
}
