package top.bogey.touch_tool.data.action.action;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinTask;

public class DoTaskAction extends NormalAction {
    private transient Pin taskPin = new Pin(new PinTask());
    private transient Pin resultPin = new Pin(new PinBoolean(), R.string.action_state_subtitle_state, PinDirection.OUT);

    public DoTaskAction() {
        super(R.string.action_do_task_action_title);
        taskPin = addPin(taskPin);
        resultPin = addPin(resultPin);
    }

    public DoTaskAction(JsonObject jsonObject) {
        super(R.string.action_do_task_action_title, jsonObject);
        taskPin = reAddPin(taskPin);
        resultPin = reAddPin(resultPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinTask pinTask = (PinTask) getPinValue(runnable, actionContext, taskPin);
        PinBoolean pinResult = (PinBoolean) resultPin.getValue();
        pinResult.setValue(true);

        boolean flag = true;
        Task task = pinTask.getTask();
        StartAction startAction = null;

        if (task == null) {
            flag = false;
        } else {
            startAction = pinTask.getStartAction();
            if (startAction == null) {
                flag = false;
            }
        }

        if (flag) {
            startAction.doAction(runnable, task, null);
        } else {
            pinResult.setValue(false);
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
