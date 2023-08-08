package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public abstract class NormalAction extends Action {
    protected transient Pin inPin = new Pin(new PinExecute(), R.string.pin_execute);
    protected transient Pin outPin = new Pin(new PinExecute(), R.string.pin_execute, true);

    public NormalAction(ActionType type) {
        super(type);
        inPin = addPin(inPin);
        outPin = addPin(outPin);
    }

    public NormalAction(JsonObject jsonObject) {
        super(jsonObject);
        inPin = reAddPin(inPin);
        outPin = reAddPin(outPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        executeNext(runnable, context, outPin);
    }

    public Pin getInPin() {
        return inPin;
    }

    public Pin getOutPin() {
        return outPin;
    }
}
