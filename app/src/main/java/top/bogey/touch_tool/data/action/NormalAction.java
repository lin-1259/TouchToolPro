package top.bogey.touch_tool.data.action;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;

public class NormalAction extends BaseAction {
    protected transient final Pin inPin;
    protected transient final Pin outPin;

    public NormalAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        inPin = addPin(new Pin(new PinExecute(), PinSlotType.MULTI));
        outPin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT, PinSlotType.SINGLE));
    }

    public NormalAction(JsonObject jsonObject) {
        super(jsonObject);
        inPin = addPin(tmpPins.remove(0));
        outPin = addPin(tmpPins.remove(0));
    }

    public Pin getInPin() {
        return inPin;
    }

    public Pin getOutPin() {
        return outPin;
    }
}
