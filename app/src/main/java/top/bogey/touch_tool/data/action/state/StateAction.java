package top.bogey.touch_tool.data.action.state;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class StateAction extends CalculateAction {
    protected transient final Pin statePin;

    public StateAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        statePin = addPin(new Pin(new PinBoolean(false), context.getString(R.string.action_state_subtitle_state), PinDirection.OUT, PinSlotType.MULTI));
    }

    public StateAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = addPin(tmpPins.remove(0));
    }
}
