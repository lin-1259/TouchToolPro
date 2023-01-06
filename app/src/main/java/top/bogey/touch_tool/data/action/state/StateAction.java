package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class StateAction extends BaseAction {
    protected final Pin<? extends PinObject> statePin;

    public StateAction() {
        super();
        statePin = addPin(new Pin<>(new PinBoolean(false), R.string.action_state_subtitle_state, PinDirection.OUT, PinSlotType.MULTI));
    }

    public StateAction(Parcel in) {
        super(in);
        statePin = addPin(pinsTmp.remove(0));
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        return false;
    }
}
