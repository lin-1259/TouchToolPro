package top.bogey.touch_tool.data.action.state;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.utils.AppUtils;

public class ScreenStateAction extends StateAction {
    private transient final Pin screenStatePin;

    public ScreenStateAction(Context context) {
        super(context, R.string.action_screen_state_title);
        screenStatePin = addPin(new Pin(new PinSpinner(context.getResources().getStringArray(R.array.screen_state)), context.getString(R.string.action_screen_state_subtitle_state)));
    }

    public ScreenStateAction(JsonObject jsonObject) {
        super(jsonObject);
        screenStatePin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin pin) {
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
