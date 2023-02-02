package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.StringRes;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class StateAction extends CalculateAction {
    protected final Pin<? extends PinObject> statePin;

    public StateAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        statePin = addPin(new Pin<>(new PinBoolean(false), context.getString(R.string.action_state_subtitle_state), PinDirection.OUT, PinSlotType.MULTI));
    }

    public StateAction(Parcel in) {
        super(in);
        statePin = addPin(pinsTmp.remove(0));
    }
}
