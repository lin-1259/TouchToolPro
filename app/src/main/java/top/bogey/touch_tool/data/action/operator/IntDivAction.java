package top.bogey.touch_tool.data.action.operator;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntDivAction extends CalculateAction {
    protected transient Pin outValuePin = new Pin(new PinInteger(), PinDirection.OUT);
    protected transient Pin originPin = new Pin(new PinInteger());
    protected transient Pin secondPin = new Pin(new PinInteger());

    public IntDivAction() {
        this(R.string.action_int_div_operator_title);
    }

    public IntDivAction(@StringRes int titleId) {
        super(titleId);
        outValuePin = addPin(outValuePin);
        originPin = addPin(originPin);
        secondPin = addPin(secondPin);
    }


    public IntDivAction(JsonObject jsonObject) {
        this(R.string.action_int_div_operator_title, jsonObject);
    }

    public IntDivAction(@StringRes int titleId, JsonObject jsonObject) {
        super(titleId, jsonObject);
        outValuePin = reAddPin(outValuePin);
        originPin = reAddPin(originPin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) outValuePin.getValue();
        value.setValue(0);

        PinInteger origin = (PinInteger) getPinValue(runnable, actionContext, originPin);
        PinInteger second = (PinInteger) getPinValue(runnable, actionContext, secondPin);
        int secondValue = second.getValue();
        if (secondValue == 0) secondValue = 1;
        value.setValue(origin.getValue() / secondValue);
    }
}
