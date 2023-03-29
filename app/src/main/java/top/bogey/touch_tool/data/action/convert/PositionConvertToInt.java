package top.bogey.touch_tool.data.action.convert;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class PositionConvertToInt extends CalculateAction {
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_int_convert_position_subtitle_position);
    private transient Pin xPin = new Pin(new PinInteger(), R.string.action_int_convert_position_subtitle_x, PinDirection.OUT);
    private transient Pin yPin = new Pin(new PinInteger(), R.string.action_int_convert_position_subtitle_y, PinDirection.OUT);

    public PositionConvertToInt() {
        super(R.string.action_position_convert_int_title);
        posPin = addPin(posPin);
        xPin = addPin(xPin);
        yPin = addPin(yPin);
    }

    public PositionConvertToInt(JsonObject jsonObject) {
        super(R.string.action_position_convert_int_title, jsonObject);
        posPin = reAddPin(posPin);
        xPin = reAddPin(xPin);
        yPin = reAddPin(yPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinPoint pos = (PinPoint) getPinValue(runnable, actionContext, posPin);
        PinInteger x = (PinInteger) xPin.getValue();
        PinInteger y = (PinInteger) yPin.getValue();
        x.setValue(pos.getX());
        y.setValue(pos.getY());
    }
}
