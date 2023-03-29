package top.bogey.touch_tool.data.action.start;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;

public class InnerStartAction extends StartAction {
    private transient Pin pin;

    public InnerStartAction(Pin pin) {
        super(0);
        this.pin = pin;
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        doNextAction(runnable, actionContext, this.pin);
    }
}
