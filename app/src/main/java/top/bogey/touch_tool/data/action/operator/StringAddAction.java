package top.bogey.touch_tool.data.action.operator;

import android.os.Parcel;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;

public class StringAddAction extends CalculateAction {
    protected final Pin<? extends PinObject> outValuePin;
    protected final Pin<? extends PinObject> firstPin;

    public StringAddAction() {
        super();
        outValuePin = addPin(new Pin<>(new PinString(), 0, PinDirection.OUT, PinSlotType.MULTI));
        firstPin = addPin(new Pin<>(new PinString()));
        addPin(new Pin<>(new PinString()));
        Pin<PinString> executePin = new Pin<>(new PinString());
        addPin(new Pin<>(new PinAdd(executePin), R.string.action_subtitle_add_pin, PinSlotType.EMPTY));
        titleId = R.string.action_string_add_operator_title;
    }

    public StringAddAction(Parcel in) {
        super(in);
        outValuePin = addPin(pinsTmp.remove(0));
        firstPin = addPin(pinsTmp.remove(0));
        for (Pin<? extends PinObject> pin : pinsTmp) {
            addPin(pin);
        }
        pinsTmp.clear();
        titleId = R.string.action_string_add_operator_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinString value = (PinString) outValuePin.getValue();

        ArrayList<Pin<? extends PinObject>> pins = getPins();
        int i = pins.indexOf(firstPin);
        StringBuilder builder = new StringBuilder();
        for (; i < pins.size() - 1; i++) {
            Pin<? extends PinObject> pinObject = pins.get(i);
            PinString result = (PinString) getPinValue(worldState, task, pinObject);
            builder.append(result.getValue());
        }
        value.setValue(builder.toString());
    }
}
