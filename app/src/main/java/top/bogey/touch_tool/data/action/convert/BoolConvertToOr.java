package top.bogey.touch_tool.data.action.convert;

import android.content.Context;
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
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class BoolConvertToOr extends CalculateAction {
    protected final Pin<? extends PinObject> outConditionPin;
    protected final Pin<? extends PinObject> firstConditionPin;

    public BoolConvertToOr(Context context) {
        super(context, R.string.action_bool_convert_or_title);
        outConditionPin = addPin(new Pin<>(new PinBoolean(), context.getString(R.string.action_state_subtitle_state), PinDirection.OUT, PinSlotType.MULTI));
        firstConditionPin = addPin(new Pin<>(new PinBoolean(), context.getString(R.string.action_bool_convert_and_subtitle_condition)));
        addPin(new Pin<>(new PinBoolean(), context.getString(R.string.action_bool_convert_and_subtitle_condition)));
        Pin<PinBoolean> executePin = new Pin<>(new PinBoolean(false), context.getString(R.string.action_bool_convert_and_subtitle_condition));
        addPin(new Pin<>(new PinAdd(executePin), context.getString(R.string.action_subtitle_add_pin), PinSlotType.EMPTY));
    }

    public BoolConvertToOr(Parcel in) {
        super(in);
        outConditionPin = addPin(pinsTmp.remove(0));
        firstConditionPin = addPin(pinsTmp.remove(0));
        for (Pin<? extends PinObject> pin : pinsTmp) {
            addPin(pin);
        }
        pinsTmp.clear();
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinBoolean value = (PinBoolean) outConditionPin.getValue();

        ArrayList<Pin<? extends PinObject>> pins = getPins();
        int i = pins.indexOf(firstConditionPin);
        for (; i < pins.size() - 1; i++) {
            Pin<? extends PinObject> pinObject = pins.get(i);
            PinBoolean resultPin = (PinBoolean) getPinValue(worldState, task, pinObject);
            if (resultPin.getValue()) {
                value.setValue(true);
            }
        }
        value.setValue(false);
    }
}
