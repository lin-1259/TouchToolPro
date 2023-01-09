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
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class PositionConvertToInt extends BaseAction {
    protected final Pin<? extends PinObject> posPin;
    protected final Pin<? extends PinObject> xPin;
    protected final Pin<? extends PinObject> yPin;

    public PositionConvertToInt() {
        super();
        posPin = addPin(new Pin<>(new PinPoint(), R.string.action_int_convert_position_subtitle_position));
        xPin = addPin(new Pin<>(new PinInteger(), R.string.action_int_convert_position_subtitle_x, PinDirection.OUT, PinSlotType.MULTI));
        yPin = addPin(new Pin<>(new PinInteger(), R.string.action_int_convert_position_subtitle_y, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_position_convert_int_title;
    }

    public PositionConvertToInt(Parcel in) {
        super(in);
        posPin = addPin(pinsTmp.remove(0));
        xPin = addPin(pinsTmp.remove(0));
        yPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_position_convert_int_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {}

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinPoint pos = (PinPoint) getPinValue(worldState, task, posPin);
        PinInteger x = (PinInteger) getPinValue(worldState, task, xPin);
        PinInteger y = (PinInteger) getPinValue(worldState, task, yPin);
        x.setValue(pos.getX());
        y.setValue(pos.getY());
    }
}
