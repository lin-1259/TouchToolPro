package top.bogey.touch_tool.data.action.state;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class StateAction extends CalculateAction {
    protected transient Pin statePin = new Pin(new PinBoolean(false), R.string.action_state_subtitle_state, PinDirection.OUT);

    public StateAction(@StringRes int titleId) {
        super(titleId);
        statePin = addPin(statePin);
    }

    public StateAction(@StringRes int titleId, JsonObject jsonObject) {
        super(titleId, jsonObject);
        statePin = reAddPin(statePin);
    }
}
