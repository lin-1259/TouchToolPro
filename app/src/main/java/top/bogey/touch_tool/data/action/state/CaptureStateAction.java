package top.bogey.touch_tool.data.action.state;

import android.content.Context;
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

    public CaptureStateAction(Context context) {
        super(context, R.string.action_capture_state_title);
    }

    public CaptureStateAction(Parcel in) {
        super(in);
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getService();
        value.setValue(service.isCaptureEnabled());
    }
}
