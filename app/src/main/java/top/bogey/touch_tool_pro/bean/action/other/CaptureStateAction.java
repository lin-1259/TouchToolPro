package top.bogey.touch_tool_pro.bean.action.other;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class CaptureStateAction extends Action {
    private transient Pin statePin = new Pin(new PinBoolean(), R.string.action_capture_state_subtitle_state, true);

    public CaptureStateAction() {
        super(ActionType.CAPTURE_STATE);
        statePin = addPin(statePin);
    }

    public CaptureStateAction(JsonObject jsonObject) {
        super(jsonObject);
        statePin = reAddPin(statePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        statePin.getValue(PinBoolean.class).setBool(service.isCaptureEnabled());
    }
}
