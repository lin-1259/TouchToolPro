package top.bogey.touch_tool.data.action.convert;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class IntConvertToPosition extends CalculateAction {
    private transient final Pin xPin;
    private transient final Pin yPin;
    private transient final Pin posPin;

    public IntConvertToPosition(Context context) {
        super(context, R.string.action_int_convert_position_title);
        xPin = addPin(new Pin(new PinInteger(), context.getString(R.string.action_int_convert_position_subtitle_x)));
        yPin = addPin(new Pin(new PinInteger(), context.getString(R.string.action_int_convert_position_subtitle_y)));
        posPin = addPin(new Pin(new PinPoint(), context.getString(R.string.action_int_convert_position_subtitle_position), PinDirection.OUT, PinSlotType.MULTI));
    }

    public IntConvertToPosition(JsonObject jsonObject) {
        super(jsonObject);
        xPin = addPin(tmpPins.remove(0));
        yPin = addPin(tmpPins.remove(0));
        posPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(ActionContext actionContext, Pin pin) {
        PinInteger x = (PinInteger) getPinValue(actionContext, xPin);
        PinInteger y = (PinInteger) getPinValue(actionContext, yPin);
        PinPoint pos = (PinPoint) posPin.getValue();
        pos.setX(x.getValue());
        pos.setY(y.getValue());
    }
}
