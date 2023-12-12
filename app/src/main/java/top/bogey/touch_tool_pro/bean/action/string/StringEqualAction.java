package top.bogey.touch_tool_pro.bean.action.string;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class StringEqualAction extends CheckAction {
    private transient Pin firstPin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin secondPin = new Pin(new PinString(), R.string.pin_string);

    public StringEqualAction() {
        super(ActionType.STRING_EQUAL);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
    }

    public StringEqualAction(JsonObject jsonObject) {
        super(jsonObject);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinString first = (PinString) getPinValue(runnable, context, firstPin);
        PinString second = (PinString) getPinValue(runnable, context, secondPin);

        result.setBool(Objects.equals(first.getValue(), second.getValue()));
    }
}
