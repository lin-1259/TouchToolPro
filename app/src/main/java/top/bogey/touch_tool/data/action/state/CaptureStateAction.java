package top.bogey.touch_tool.data.action.state;

import android.os.Parcel;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class CaptureStateAction extends StateAction {

    public CaptureStateAction() {
        super();
        titleId = R.string.action_capture_state_title;
    }

    public CaptureStateAction(Parcel in) {
        super(in);
        titleId = R.string.action_capture_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(statePin.getId())) return;
        PinBoolean value = (PinBoolean) getPinValue(worldState, task, statePin);
        MainAccessibilityService service = MainApplication.getService();
        value.setValue(service.isCaptureEnabled());
    }
}
