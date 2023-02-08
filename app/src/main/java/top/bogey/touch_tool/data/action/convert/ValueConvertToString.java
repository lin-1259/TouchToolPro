package top.bogey.touch_tool.data.action.convert;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.data.pin.object.PinValue;

public class ValueConvertToString extends CalculateAction {
    private transient final Pin valuePin;
    private transient final Pin stringPin;

    public ValueConvertToString(Context context) {
        super(context, R.string.action_value_convert_string_title);
        valuePin = addPin(new Pin(new PinValue(), context.getString(R.string.action_value_convert_string_subtitle_value)));
        stringPin = addPin(new Pin(new PinString(), context.getString(R.string.action_value_convert_string_subtitle_string), PinDirection.OUT, PinSlotType.MULTI));
    }

    public ValueConvertToString(JsonObject jsonObject) {
        super(jsonObject);
        valuePin = addPin(tmpPins.remove(0));
        stringPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin pin) {
        PinValue value = (PinValue) getPinValue(worldState, task, valuePin);
        PinString string = (PinString) stringPin.getValue();
        string.setValue(value.toString());
    }
}
