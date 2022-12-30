package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinBoolean;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;

public class ManualStartAction extends StartAction {
    private final Pin<? extends PinObject> restartPin;

    public ManualStartAction() {
        super();
        restartPin = addPin(new Pin<>(new PinSpinner(R.array.restart_type), R.string.action_start_subtitle_restart));
        titleId = R.string.action_manual_start_title;
    }

    public ManualStartAction(Parcel in) {
        super(in);
        restartPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_manual_start_title;
    }

    @Override
    public RestartType getRestartType() {
        PinSpinner value = (PinSpinner) restartPin.getValue();
        return RestartType.values()[value.getIndex()];
    }
}
