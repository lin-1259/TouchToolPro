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
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class PositionConvertToInt extends CalculateAction {
    private transient final Pin<?> posPin;
    private transient final Pin<?> xPin;
    private transient final Pin<?> yPin;

    public PositionConvertToInt(Context context) {
        super(context, R.string.action_position_convert_int_title);
        posPin = addPin(new Pin<>(new PinPoint(), context.getString(R.string.action_int_convert_position_subtitle_position)));
        xPin = addPin(new Pin<>(new PinInteger(), context.getString(R.string.action_int_convert_position_subtitle_x), PinDirection.OUT, PinSlotType.MULTI));
        yPin = addPin(new Pin<>(new PinInteger(), context.getString(R.string.action_int_convert_position_subtitle_y), PinDirection.OUT, PinSlotType.MULTI));
    }

    public PositionConvertToInt(JsonObject jsonObject) {
        super(jsonObject);
        posPin = addPin(tmpPins.remove(0));
        xPin = addPin(tmpPins.remove(0));
        yPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<?> pin) {
        PinPoint pos = (PinPoint) getPinValue(worldState, task, posPin);
        PinInteger x = (PinInteger) xPin.getValue();
        PinInteger y = (PinInteger) yPin.getValue();
        x.setValue(pos.getX());
        y.setValue(pos.getY());
    }
}
