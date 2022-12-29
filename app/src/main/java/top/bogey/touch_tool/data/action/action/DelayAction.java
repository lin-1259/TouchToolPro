package top.bogey.touch_tool.data.action.action;

import android.os.Parcel;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinTimeArea;

public class DelayAction extends NormalAction {
    private final Pin<? extends PinObject> delayPin;

    public DelayAction() {
        super();
        delayPin = addPin(new Pin<>(new PinTimeArea(300, TimeUnit.MILLISECONDS)));
        titleId = R.string.action_type_delay;
    }

    public DelayAction(Parcel in) {
        super(in);
        delayPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_type_delay;
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        PinTimeArea pinTimeArea = (PinTimeArea) delayPin.getValue();
        boolean sleep = sleep(pinTimeArea.getRandomTime());
        if (sleep) return super.doAction(worldState, runnable);
        return false;
    }
}
