package top.bogey.touch_tool.data.action.convert;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.data.pin.object.PinValue;

public class ValueConvertToString extends BaseAction {
    protected final Pin<? extends PinObject> valuePin;
    protected final Pin<? extends PinObject> stringPin;

    public ValueConvertToString() {
        super();
        valuePin = addPin(new Pin<>(new PinValue(), R.string.action_convert_subtitle_object));
        stringPin = addPin(new Pin<>(new PinString(), R.string.action_convert_subtitle_string, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_value_convert_string_subtitle_object;
    }

    public ValueConvertToString(Parcel in) {
        super(in);
        valuePin = addPin(pinsTmp.remove(0));
        stringPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_value_convert_string_subtitle_object;
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        return false;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task) {
        PinValue value = (PinValue) getPinValue(worldState, task, valuePin);
        PinString string = (PinString) getPinValue(worldState, task, stringPin);
        string.setValue(value.toString());
    }
}
