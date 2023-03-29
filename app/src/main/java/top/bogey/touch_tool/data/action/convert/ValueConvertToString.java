package top.bogey.touch_tool.data.action.convert;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.data.pin.object.PinValue;

public class ValueConvertToString extends CalculateAction {
    private transient Pin valuePin = new Pin(new PinValue(), R.string.action_value_convert_string_subtitle_value);
    private transient Pin stringPin = new Pin(new PinString(), R.string.action_value_convert_string_subtitle_string, PinDirection.OUT);

    public ValueConvertToString() {
        super(R.string.action_value_convert_string_title);
        valuePin = addPin(valuePin);
        stringPin = addPin(stringPin);
    }

    public ValueConvertToString(JsonObject jsonObject) {
        super(R.string.action_value_convert_string_title, jsonObject);
        valuePin = reAddPin(valuePin);
        stringPin = reAddPin(stringPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinValue value = (PinValue) getPinValue(runnable, actionContext, valuePin);
        PinString string = (PinString) stringPin.getValue();
        string.setValue(value.toString());
    }
}
