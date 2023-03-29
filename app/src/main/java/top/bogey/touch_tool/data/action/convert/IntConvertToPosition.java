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

public class IntConvertToPosition extends CalculateAction {
    private transient Pin xPin = new Pin(new PinInteger(), R.string.action_int_convert_position_subtitle_x);
    private transient Pin yPin = new Pin(new PinInteger(), R.string.action_int_convert_position_subtitle_y);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_int_convert_position_subtitle_position, PinDirection.OUT);

    public IntConvertToPosition() {
        super(R.string.action_int_convert_position_title);
        xPin = addPin(xPin);
        yPin = addPin(yPin);
        posPin = addPin(posPin);
    }

    public IntConvertToPosition(JsonObject jsonObject) {
        super(R.string.action_int_convert_position_title, jsonObject);
        xPin = reAddPin(xPin);
        yPin = reAddPin(yPin);
        posPin = reAddPin(posPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinInteger x = (PinInteger) getPinValue(runnable, actionContext, xPin);
        PinInteger y = (PinInteger) getPinValue(runnable, actionContext, yPin);
        PinPoint pos = (PinPoint) posPin.getValue();
        pos.setX(x.getValue());
        pos.setY(y.getValue());
    }
}
