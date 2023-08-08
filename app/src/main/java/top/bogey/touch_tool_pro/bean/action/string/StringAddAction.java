package top.bogey.touch_tool_pro.bean.action.string;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class StringAddAction extends Action {
    private transient Pin resultPin = new Pin(new PinString(), R.string.pin_string, true);
    private transient Pin firstPin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin secondPin = new Pin(new PinString(), R.string.pin_string);
    private final transient Pin morePin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_pin);
    private final transient ArrayList<Pin> stringPins = new ArrayList<>();

    public StringAddAction() {
        super(ActionType.STRING_ADD);
        resultPin = addPin(resultPin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public StringAddAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        stringPins.add(firstPin = reAddPin(firstPin));
        stringPins.add(secondPin = reAddPin(secondPin));
        stringPins.addAll(reAddPin(morePin, 1));
        addPin = reAddPin(addPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString result = resultPin.getValue(PinString.class);

        StringBuilder builder = new StringBuilder();
        for (Pin stringPin : stringPins) {
            PinString string = (PinString) getPinValue(runnable, context, stringPin);
            if (string.getValue() == null || string.getValue().isEmpty()) continue;
            builder.append(string.getValue());
        }

        result.setValue(builder.toString());
    }
}
