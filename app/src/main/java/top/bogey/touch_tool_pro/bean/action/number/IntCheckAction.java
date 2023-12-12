package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public abstract class IntCheckAction extends CheckAction {
    protected transient Pin firstPin = new Pin(new PinInteger(), R.string.pin_int);
    protected transient Pin secondPin = new Pin(new PinInteger(), R.string.pin_int);

    public IntCheckAction(ActionType type) {
        super(type);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
    }

    public IntCheckAction(JsonObject jsonObject) {
        super(jsonObject);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    public abstract void calculate(TaskRunnable runnable, FunctionContext context, Pin pin);
}
