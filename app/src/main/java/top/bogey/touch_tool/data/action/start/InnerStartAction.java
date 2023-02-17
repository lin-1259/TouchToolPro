package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import java.util.HashSet;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;

public class InnerStartAction extends StartAction {
    private transient final Pin pin;

    public InnerStartAction(Context context, Pin pin) {
        super(context, 0);
        this.pin = pin;
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        doNextAction(runnable, actionContext, this.pin);
    }
}
