package top.bogey.touch_tool.data.action.convert;

import android.os.Parcel;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class BoolConvertToAnd extends BaseAction {
    protected final Pin<? extends PinObject> outConditionPin;
    protected final Pin<? extends PinObject> firstConditionPin;

    public BoolConvertToAnd() {
        super();
        outConditionPin = addPin(new Pin<>(new PinBoolean(), R.string.action_state_subtitle_state, PinDirection.OUT));
        firstConditionPin = addPin(new Pin<>(new PinBoolean(), R.string.action_bool_convert_and_subtitle_condition));
        addPin(new Pin<>(new PinBoolean(), R.string.action_bool_convert_and_subtitle_condition));
        Pin<PinBoolean> executePin = new Pin<>(new PinBoolean(false), R.string.action_bool_convert_and_subtitle_condition);
        addPin(new Pin<>(new PinAdd(executePin), R.string.action_subtitle_add_pin));
        titleId = R.string.action_bool_convert_and_title;
    }

    public BoolConvertToAnd(Parcel in) {
        super(in);
        outConditionPin = addPin(pinsTmp.remove(0));
        firstConditionPin = addPin(pinsTmp.remove(0));
        for (Pin<? extends PinObject> pin : pinsTmp) {
            addPin(pin);
        }
        pinsTmp.clear();
        titleId = R.string.action_bool_convert_and_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(outConditionPin.getId())) return;
        PinBoolean value = (PinBoolean) getPinValue(worldState, task, outConditionPin);

        ArrayList<Pin<? extends PinObject>> pins = getPins();
        int i = pins.indexOf(firstConditionPin);
        for (; i < pins.size() - 1; i++) {
            Pin<? extends PinObject> pinObject = pins.get(i);
            PinBoolean result = (PinBoolean) getPinValue(worldState, task, pinObject);
            value.setValue(value.getValue() && result.getValue());
        }
    }
}
