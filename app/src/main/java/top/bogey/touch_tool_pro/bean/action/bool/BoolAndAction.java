package top.bogey.touch_tool_pro.bean.action.bool;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.check.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class BoolAndAction extends CheckAction {
    private transient Pin firstPin = new Pin(new PinBoolean(), R.string.pin_boolean);
    private transient Pin secondPin = new Pin(new PinBoolean(), R.string.pin_boolean);
    private final transient Pin morePin = new Pin(new PinBoolean(), R.string.pin_boolean);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_pin);
    private final transient ArrayList<Pin> boolPins = new ArrayList<>();

    public BoolAndAction() {
        super(ActionType.BOOL_AND);
        boolPins.add(firstPin = addPin(firstPin));
        boolPins.add(secondPin = addPin(secondPin));
        addPin = addPin(addPin);
    }

    public BoolAndAction(JsonObject jsonObject) {
        super(jsonObject);
        boolPins.add(firstPin = reAddPin(firstPin));
        boolPins.add(secondPin = reAddPin(secondPin));
        boolPins.addAll(reAddPin(morePin, 1));
        addPin = reAddPin(addPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);
        result.setBool(false);

        for (Pin boolPin : boolPins) {
            PinBoolean bool = (PinBoolean) getPinValue(runnable, context, boolPin);
            if (!bool.isBool()) return;
        }

        result.setBool(true);
    }
}
