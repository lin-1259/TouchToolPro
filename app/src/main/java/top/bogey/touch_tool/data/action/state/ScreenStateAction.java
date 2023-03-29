package top.bogey.touch_tool.data.action.state;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinSpinner;
import top.bogey.touch_tool.utils.AppUtils;

public class ScreenStateAction extends StateAction {
    private transient Pin screenStatePin = new Pin(new PinSpinner(R.array.screen_state), R.string.action_screen_state_subtitle_state);

    public ScreenStateAction() {
        super(R.string.action_screen_state_title);
        screenStatePin = addPin(screenStatePin);
    }

    public ScreenStateAction(JsonObject jsonObject) {
        super(R.string.action_screen_state_title, jsonObject);
        screenStatePin = reAddPin(screenStatePin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        ScreenState state = AppUtils.getScreenState(MainApplication.getInstance().getService());
        int screenState = ((PinSpinner) getPinValue(runnable, actionContext, screenStatePin)).getIndex();
        value.setValue(state.ordinal() == screenState);
    }

    public enum ScreenState {
        SCREEN_OFF,
        SCREEN_LOCKED,
        SCREEN_UNLOCKED
    }
}
