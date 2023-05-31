package top.bogey.touch_tool.data.action.action;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class CaptureServiceAction extends NormalAction {
    private transient Pin statePin = new Pin(new PinBoolean(true), R.string.action_open_capture_subtitle_state);

    public CaptureServiceAction() {
        super(R.string.action_open_capture_action_title);
        statePin = addPin(statePin);
    }

    public CaptureServiceAction(JsonObject jsonObject) {
        super(R.string.action_open_capture_action_title, jsonObject);
        statePin = reAddPin(statePin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean state = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (state.getValue()) {
            service.startCaptureService(true, null);
        } else {
            service.stopCaptureService();
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
