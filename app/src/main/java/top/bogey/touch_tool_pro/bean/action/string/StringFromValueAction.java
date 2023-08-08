package top.bogey.touch_tool_pro.bean.action.string;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class StringFromValueAction extends Action {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string, true);
    private transient Pin valuePin = new Pin(new PinValue(), R.string.pin_value);

    public StringFromValueAction() {
        super(ActionType.STRING_FROM_VALUE);
        textPin = addPin(textPin);
        valuePin = addPin(valuePin);
    }

    public StringFromValueAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = reAddPin(textPin);
        valuePin = reAddPin(valuePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        textPin.getValue(PinString.class).setValue(value.toString());
    }
}
