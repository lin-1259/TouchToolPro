package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;

public class StartAction extends BaseAction {
    protected final Pin<? extends PinObject> restartPin;

    public StartAction() {
        super();
        addPin(outPin);
        restartPin = addPin(new Pin<>(new PinSpinner(R.array.restart_type), R.string.action_start_subtitle_restart));
    }

    public StartAction(Parcel in) {
        super(in);
        outPin = addPin(pinsTmp.remove(0));
        restartPin = addPin(pinsTmp.remove(0));
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        if (!checkReady(worldState, runnable.getTask())) return false;
        return super.doAction(worldState, runnable);
    }

    public boolean checkReady(WorldState worldState, Task task) {
        return true;
    }

    public RestartType getRestartType() {
        return RestartType.START_NEW;
    }
}
