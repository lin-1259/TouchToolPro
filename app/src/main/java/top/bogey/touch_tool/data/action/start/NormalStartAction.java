package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinBoolean;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;

public class NormalStartAction extends StartAction {
    private final Pin<? extends PinObject> startPin;

    public NormalStartAction() {
        super();
        startPin = addPin(new Pin<>(new PinBoolean(false), R.string.action_normal_start_subtitle_condition));
        titleId = R.string.action_normal_start_title;
    }

    public NormalStartAction(Parcel in) {
        super(in);
        startPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_normal_start_title;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        PinBoolean value = (PinBoolean) getPinValue(worldState, task, startPin);
        return value.getValue();
    }

    @Override
    public RestartType getRestartType() {
        PinSpinner value = (PinSpinner) restartPin.getValue();
        return RestartType.values()[value.getIndex()];
    }
}
