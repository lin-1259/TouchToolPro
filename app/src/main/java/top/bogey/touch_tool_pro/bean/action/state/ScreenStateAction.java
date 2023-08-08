package top.bogey.touch_tool_pro.bean.action.state;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class ScreenStateAction extends Action {
    private transient Pin statePin = new Pin(new PinSpinner(R.array.screen_state), R.string.action_screen_state_subtitle_state, true);

    public ScreenStateAction() {
        super(ActionType.SCREEN_STATE);
        statePin = addPin(statePin);
    }

    public ScreenStateAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ScreenState state = AppUtils.getScreenState(MainApplication.getInstance());
        statePin.getValue(PinSpinner.class).setIndex(state.ordinal());
    }

    public enum ScreenState {
        OFF, LOCKED, ON
    }
}
