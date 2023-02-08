package top.bogey.touch_tool.data.action.convert;

import android.content.Context;

import com.google.gson.JsonObject;

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

public class BoolConvertToOr extends CalculateAction {
    private transient final Pin outConditionPin;
    private transient final Pin firstConditionPin;

    public BoolConvertToOr(Context context) {
        super(context, R.string.action_bool_convert_or_title);
        outConditionPin = addPin(new Pin(new PinBoolean(), context.getString(R.string.action_state_subtitle_state), PinDirection.OUT, PinSlotType.MULTI));
        firstConditionPin = addPin(new Pin(new PinBoolean(), context.getString(R.string.action_bool_convert_and_subtitle_condition)));
        addPin(new Pin(new PinBoolean(), context.getString(R.string.action_bool_convert_and_subtitle_condition)));
        Pin executePin = new Pin(new PinBoolean(false), context.getString(R.string.action_bool_convert_and_subtitle_condition));
        addPin(new Pin(new PinAdd(executePin), context.getString(R.string.action_subtitle_add_pin), PinSlotType.EMPTY));
    }

    public BoolConvertToOr(JsonObject jsonObject) {
        super(jsonObject);
        outConditionPin = addPin(tmpPins.remove(0));
        firstConditionPin = addPin(tmpPins.remove(0));
        for (Pin pin : tmpPins) {
            addPin(pin);
        }
        tmpPins.clear();
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin pin) {
        PinBoolean value = (PinBoolean) outConditionPin.getValue();

        ArrayList<Pin> pins = getPins();
        int i = pins.indexOf(firstConditionPin);
        for (; i < pins.size() - 1; i++) {
            Pin pinObject = pins.get(i);
            PinBoolean resultPin = (PinBoolean) getPinValue(worldState, task, pinObject);
            if (resultPin.getValue()) {
                value.setValue(true);
                return;
            }
        }
        value.setValue(false);
    }
}
