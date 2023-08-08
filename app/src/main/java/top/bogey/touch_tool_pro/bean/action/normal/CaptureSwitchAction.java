package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class CaptureSwitchAction extends NormalAction {
    private transient Pin capturePin = new Pin(new PinBoolean(true), R.string.action_open_capture_subtitle_state);
    private transient Pin waitPin = new Pin(new PinBoolean(false), R.string.action_open_capture_subtitle_wait);

    public CaptureSwitchAction() {
        super(ActionType.CAPTURE_SWITCH);
        capturePin = addPin(capturePin);
        waitPin = addPin(waitPin);
    }

    public CaptureSwitchAction(JsonObject jsonObject) {
        super(jsonObject);
        capturePin = reAddPin(capturePin);
        waitPin = reAddPin(waitPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean capture = (PinBoolean) getPinValue(runnable, context, capturePin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (capture.isBool()) {
            PinBoolean wait = (PinBoolean) getPinValue(runnable, context, waitPin);
            if (wait.isBool()) {
                service.startCaptureService(true, result -> runnable.resume());
                runnable.pause();
            } else {
                service.startCaptureService(true, null);
            }
        } else {
            service.stopCaptureService();
        }
        runnable.sleep(100);
        executeNext(runnable, context, outPin);
    }
}
