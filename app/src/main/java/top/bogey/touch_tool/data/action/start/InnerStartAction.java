package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StartAction;
import top.bogey.touch_tool.data.pin.Pin;

public class InnerStartAction extends StartAction {
    private transient final Pin pin;

    public InnerStartAction(Context context, Pin pin) {
        super(context, 0);
        this.pin = pin;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        doAction(worldState, runnable, pin);
    }
}
