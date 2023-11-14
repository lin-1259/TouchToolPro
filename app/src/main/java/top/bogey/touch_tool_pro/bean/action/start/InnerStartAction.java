package top.bogey.touch_tool_pro.bean.action.start;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class InnerStartAction extends StartAction {
    private final transient Pin pin;

    public InnerStartAction(Pin pin) {
        super(ActionType.INNER_START);
        this.pin = pin;
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        executeNext(runnable, context, this.pin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }

    @Override
    public RestartType getRestartType() {
        return RestartType.NEW;
    }
}
