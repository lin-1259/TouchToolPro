package top.bogey.touch_tool.data.action.action;

import android.os.Parcel;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class CaptureServiceAction extends NormalAction {
    private final Pin<? extends PinObject> statePin;

    public CaptureServiceAction() {
        super();
        statePin = addPin(new Pin<>(new PinBoolean(true), R.string.action_open_capture_subtitle_state));
        titleId = R.string.action_open_capture_action_title;
    }

    public CaptureServiceAction(Parcel in) {
        super(in);
        statePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_open_capture_action_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        PinBoolean state = (PinBoolean) getPinValue(worldState, runnable.getTask(), statePin);
        MainAccessibilityService service = MainApplication.getService();
        if (state.getValue()) {
            service.startCaptureService(true, null);
        } else {
            service.stopCaptureService();
        }
        super.doAction(worldState, runnable);
    }
}
