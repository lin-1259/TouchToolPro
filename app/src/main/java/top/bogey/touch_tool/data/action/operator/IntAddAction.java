package top.bogey.touch_tool.data.action.operator;

import android.os.Parcel;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class IntAddAction extends BaseAction {
    protected final Pin<? extends PinObject> outValuePin;
    protected final Pin<? extends PinObject> firstPin;

    public IntAddAction() {
        super();
        outValuePin = addPin(new Pin<>(new PinInteger(), 0, PinDirection.OUT, PinSlotType.MULTI));
        firstPin = addPin(new Pin<>(new PinInteger()));
        addPin(new Pin<>(new PinInteger()));
        Pin<PinInteger> executePin = new Pin<>(new PinInteger());
        addPin(new Pin<>(new PinAdd(executePin), R.string.action_subtitle_add_pin, PinSlotType.EMPTY));
        titleId = R.string.action_int_add_operator_title;
    }

    public IntAddAction(Parcel in) {
        super(in);
        outValuePin = addPin(pinsTmp.remove(0));
        firstPin = addPin(pinsTmp.remove(0));
        for (Pin<? extends PinObject> pin : pinsTmp) {
            addPin(pin);
        }
        pinsTmp.clear();
        titleId = R.string.action_int_add_operator_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) getPinValue(worldState, task, outValuePin);

        ArrayList<Pin<? extends PinObject>> pins = getPins();
        int i = pins.indexOf(firstPin);
        for (; i < pins.size() - 1; i++) {
            Pin<? extends PinObject> pinObject = pins.get(i);
            PinInteger result = (PinInteger) getPinValue(worldState, task, pinObject);
            value.setValue(value.getValue() + result.getValue());
        }
    }
}
