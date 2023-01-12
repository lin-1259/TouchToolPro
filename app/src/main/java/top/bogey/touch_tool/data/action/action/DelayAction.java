package top.bogey.touch_tool.data.action.action;

import android.os.Parcel;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinTimeArea;

public class DelayAction extends NormalAction {
    private final Pin<? extends PinObject> delayPin;

    public DelayAction() {
        super();
        delayPin = addPin(new Pin<>(new PinTimeArea(300, TimeUnit.MILLISECONDS)));
        titleId = R.string.action_delay_action_title;
    }

    public DelayAction(Parcel in) {
        super(in);
        delayPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_delay_action_title;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinTimeArea pinTimeArea = (PinTimeArea) getPinValue(worldState, runnable.getTask(), delayPin);
        boolean sleep = sleep(pinTimeArea.getRandomTime());
        if (!sleep) return;
        super.doAction(worldState, runnable);
    }
}
