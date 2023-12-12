package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public abstract class IntMoreAction extends Action implements ActionMorePinInterface {
    private final transient Pin morePin = new Pin(new PinInteger(), R.string.pin_int);
    protected transient Pin resultPin = new Pin(new PinInteger(), R.string.pin_int, true);
    private transient Pin firstPin = new Pin(new PinInteger(), R.string.pin_int);
    private transient Pin secondPin = new Pin(new PinInteger(), R.string.pin_int);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_pin);

    public IntMoreAction(ActionType type) {
        super(type);
        resultPin = addPin(resultPin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public IntMoreAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
        reAddPin(morePin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    public abstract void calculate(TaskRunnable runnable, FunctionContext context, Pin pin);

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == firstPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
