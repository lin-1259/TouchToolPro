package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class BreakTaskAction extends Action {
    private transient Pin inPin = new Pin(new PinExecute(), R.string.pin_execute);

    public BreakTaskAction() {
        super(ActionType.BREAK_TASK);
        inPin = addPin(inPin);
    }

    public BreakTaskAction(JsonObject jsonObject) {
        super(jsonObject);
        inPin = reAddPin(inPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        runnable.stop();
    }
}
