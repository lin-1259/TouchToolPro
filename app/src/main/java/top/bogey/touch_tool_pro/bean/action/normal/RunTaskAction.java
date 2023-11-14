package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class RunTaskAction extends NormalAction {
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin taskPin = new Pin(new PinTask());

    public RunTaskAction() {
        super(ActionType.RUN_TASK);
        falsePin = addPin(falsePin);
        taskPin = addPin(taskPin);
    }

    public RunTaskAction(JsonObject jsonObject) {
        super(jsonObject);
        falsePin = reAddPin(falsePin);
        taskPin = reAddPin(taskPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinTask pinTask = (PinTask) getPinValue(runnable, context, taskPin);
        Task task = pinTask.getTask();
        if (task != null) {
            StartAction startAction = pinTask.getStartAction();
            if (startAction != null) {
                startAction.execute(runnable, (FunctionContext) task.copy(), null);
                executeNext(runnable, context, outPin);
                return;
            }
        }
        executeNext(runnable, context, falsePin);
    }

    public Pin getTaskPin() {
        return taskPin;
    }
}
