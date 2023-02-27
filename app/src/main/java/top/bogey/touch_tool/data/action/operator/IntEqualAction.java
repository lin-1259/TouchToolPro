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
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntEqualAction extends CalculateAction {
    protected transient final Pin outValuePin;
    protected transient final Pin originPin;
    protected transient final Pin secondPin;

    public IntEqualAction(Context context) {
        this(context, R.string.action_int_equal_operator_title);
    }

    public IntEqualAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        outValuePin = addPin(new Pin(new PinBoolean(), PinDirection.OUT, PinSlotType.MULTI));
        originPin = addPin(new Pin(new PinInteger()));
        secondPin = addPin(new Pin(new PinInteger()));
    }

    public IntEqualAction(JsonObject jsonObject) {
        super(jsonObject);
        outValuePin = addPin(tmpPins.remove(0));
        originPin = addPin(tmpPins.remove(0));
        secondPin = addPin(tmpPins.remove(0));
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
