package top.bogey.touch_tool.data.action.operator;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntDivAction extends CalculateAction {
    protected transient final Pin outValuePin;
    protected transient final Pin originPin;
    protected transient final Pin secondPin;

    public IntDivAction(Context context) {
        this(context, R.string.action_int_div_operator_title);
    }

    public IntDivAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        outValuePin = addPin(new Pin(new PinInteger(), PinDirection.OUT, PinSlotType.MULTI));
        originPin = addPin(new Pin(new PinInteger()));
        secondPin = addPin(new Pin(new PinInteger()));
    }

    public IntDivAction(JsonObject jsonObject) {
        super(jsonObject);
        outValuePin = addPin(tmpPins.remove(0));
        originPin = addPin(tmpPins.remove(0));
        secondPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) outValuePin.getValue();

        PinInteger origin = (PinInteger) getPinValue(runnable, actionContext, originPin);
        PinInteger second = (PinInteger) getPinValue(runnable, actionContext, secondPin);
        int secondValue = second.getValue();
        if (secondValue == 0) secondValue = 1;
        value.setValue(origin.getValue() / secondValue);
    }
}
