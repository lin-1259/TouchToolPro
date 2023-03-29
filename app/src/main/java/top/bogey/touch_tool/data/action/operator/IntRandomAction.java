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

public class IntRandomAction extends CalculateAction {
    protected transient Pin outValuePin = new Pin(new PinInteger(), PinDirection.OUT);
    protected transient Pin originPin = new Pin(new PinInteger(1), R.string.action_for_loop_logic_subtitle_start);
    protected transient Pin secondPin = new Pin(new PinInteger(5), R.string.action_for_loop_logic_subtitle_end);

    public IntRandomAction() {
        this(R.string.action_int_random_operator_title);
    }

    public IntRandomAction(@StringRes int titleId) {
        super(titleId);
        outValuePin = addPin(outValuePin);
        originPin = addPin(originPin);
        secondPin = addPin(secondPin);
    }

    public IntRandomAction(JsonObject jsonObject) {
        this(R.string.action_int_random_operator_title, jsonObject);
    }

    public IntRandomAction(@StringRes int titleId, JsonObject jsonObject) {
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
        double randomValue = Math.random() * (second.getValue() - origin.getValue()) + origin.getValue();
        value.setValue((int) Math.round(randomValue));
    }
}
