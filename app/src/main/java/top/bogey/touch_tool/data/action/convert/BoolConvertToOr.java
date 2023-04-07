package top.bogey.touch_tool.data.action.convert;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class BoolConvertToOr extends CalculateAction {
    private transient Pin outConditionPin = new Pin(new PinBoolean(), R.string.action_state_subtitle_state, PinDirection.OUT);
    private transient Pin firstConditionPin = new Pin(new PinBoolean(), R.string.action_bool_convert_and_subtitle_condition);
    private transient Pin secondConditionPin = new Pin(new PinBoolean(), R.string.action_bool_convert_and_subtitle_condition);
    private final transient Pin conditionPin = new Pin(new PinBoolean(), R.string.action_bool_convert_and_subtitle_condition);
    private transient Pin addPin = new Pin(new PinAdd(conditionPin), R.string.action_subtitle_add_pin);

    public BoolConvertToOr() {
        super(R.string.action_bool_convert_or_title);
        outConditionPin = addPin(outConditionPin);
        firstConditionPin = addPin(firstConditionPin);
        secondConditionPin = addPin(secondConditionPin);
        addPin = addPin(addPin);
    }

    public BoolConvertToOr(JsonObject jsonObject) {
        super(R.string.action_bool_convert_or_title, jsonObject);
        outConditionPin = reAddPin(outConditionPin);
        firstConditionPin = reAddPin(firstConditionPin);
        secondConditionPin = reAddPin(secondConditionPin);
        reAddPin(conditionPin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) outConditionPin.getValue();

        ArrayList<Pin> pins = getPins();
        int i = pins.indexOf(firstConditionPin);
        for (; i < pins.size() - 1; i++) {
            Pin pinObject = pins.get(i);
            PinBoolean resultPin = (PinBoolean) getPinValue(runnable, actionContext, pinObject);
            if (resultPin.getValue()) {
                value.setValue(true);
                return;
            }
        }
        value.setValue(false);
    }
}
