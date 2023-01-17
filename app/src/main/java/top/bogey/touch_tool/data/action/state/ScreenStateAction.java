package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.utils.AppUtils;

public class ScreenStateAction extends StateAction {
    private final Pin<? extends PinObject> screenStatePin;

    public ScreenStateAction() {
        super();
        screenStatePin = addPin(new Pin<>(new PinSpinner(R.array.screen_state), R.string.action_screen_state_subtitle_state));
        titleId = R.string.action_screen_state_title;
    }

    public ScreenStateAction(Parcel in) {
        super(in);
        screenStatePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_screen_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        ScreenState state = AppUtils.getScreenState(MainApplication.getService());
        int screenState = ((PinSpinner) getPinValue(worldState, task, screenStatePin)).getIndex();
        value.setValue(state.ordinal() == screenState);
    }

    public enum ScreenState {
        SCREEN_OFF,
        SCREEN_LOCKED,
        SCREEN_UNLOCKED
    }
}
