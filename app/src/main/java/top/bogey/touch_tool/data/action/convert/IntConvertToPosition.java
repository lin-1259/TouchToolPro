package top.bogey.touch_tool.data.action.convert;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class IntConvertToPosition extends CalculateAction {
    protected final Pin<? extends PinObject> xPin;
    protected final Pin<? extends PinObject> yPin;
    protected final Pin<? extends PinObject> posPin;

    public IntConvertToPosition() {
        super();
        xPin = addPin(new Pin<>(new PinInteger(), R.string.action_int_convert_position_subtitle_x));
        yPin = addPin(new Pin<>(new PinInteger(), R.string.action_int_convert_position_subtitle_y));
        posPin = addPin(new Pin<>(new PinPoint(), R.string.action_int_convert_position_subtitle_position, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_int_convert_position_title;
    }

    public IntConvertToPosition(Parcel in) {
        super(in);
        xPin = addPin(pinsTmp.remove(0));
        yPin = addPin(pinsTmp.remove(0));
        posPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_int_convert_position_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinInteger x = (PinInteger) getPinValue(worldState, task, xPin);
        PinInteger y = (PinInteger) getPinValue(worldState, task, yPin);
        PinPoint pos = (PinPoint) posPin.getValue();
        pos.setX(x.getValue());
        pos.setY(y.getValue());
    }
}
