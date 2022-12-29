package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;
import top.bogey.touch_tool.data.action.pin.PinSubType;

public class StartAction extends BaseAction {
    protected Pin<? extends PinObject> restartPin;

    public StartAction() {
        super();
        addPin(outPin);
        restartPin = new Pin<>(new PinSpinner(R.array.restart_type), R.string.task_restart_tips);
    }

    public StartAction(Parcel in) {
        super(in);
        outPin = addPin(pinsTmp.remove(0));
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        if (!checkReady(worldState, runnable)) return false;
        return super.doAction(worldState, runnable);
    }
}
