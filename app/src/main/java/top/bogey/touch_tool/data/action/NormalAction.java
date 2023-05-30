package top.bogey.touch_tool.data.action;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinExecute;

public class NormalAction extends BaseAction {
    protected transient Pin inPin = new Pin(new PinExecute());
    protected transient Pin outPin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);

    public NormalAction(@StringRes int titleId) {
        super(titleId);
        inPin = addPin(inPin);
        outPin = addPin(outPin);
    }

    public NormalAction(@StringRes int titleId, JsonObject jsonObject) {
        super(titleId, jsonObject);
        inPin = reAddPin(inPin);
        outPin = reAddPin(outPin);
    }

    public Pin getInPin() {
        return inPin;
    }

    public Pin getOutPin() {
        return outPin;
    }
}
