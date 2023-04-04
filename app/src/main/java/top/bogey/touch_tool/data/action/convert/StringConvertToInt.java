package top.bogey.touch_tool.data.action.convert;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinString;

public class StringConvertToInt extends CalculateAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.action_string_convert_int_subtitle_string);
    private transient Pin valuePin = new Pin(new PinInteger(), R.string.action_string_convert_int_subtitle_value, PinDirection.OUT);

    public StringConvertToInt() {
        super(R.string.action_string_convert_int_title);
        textPin = addPin(textPin);
        valuePin = addPin(valuePin);
    }

    public StringConvertToInt(JsonObject jsonObject) {
        super(R.string.action_string_convert_int_title, jsonObject);
        textPin = reAddPin(textPin);
        valuePin = reAddPin(valuePin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinString text = (PinString) getPinValue(runnable, actionContext, textPin);
        PinInteger value = (PinInteger) valuePin.getValue();
        if (text.getValue() == null) return;
        Pattern pattern = Pattern.compile("^.*?(\\d+)");
        Matcher matcher = pattern.matcher(text.getValue());
        if (matcher.find()) {
            String group = matcher.group(1);
            value.setParamValue(group);
        }
    }
}
