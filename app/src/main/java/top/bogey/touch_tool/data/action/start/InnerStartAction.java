package top.bogey.touch_tool.data.action.start;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;

public class InnerStartAction extends StartAction {
    private final transient Pin pin;

    public InnerStartAction(Pin pin) {
        super(R.string.action_inner_start_title);
        this.pin = pin;
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        doNextAction(runnable, actionContext, this.pin);
    }

    @Override
    public RestartType getRestartType() {
        return RestartType.START_NEW;
    }
}
