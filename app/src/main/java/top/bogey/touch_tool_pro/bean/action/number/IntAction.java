package top.bogey.touch_tool_pro.bean.action.number;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public abstract class IntAction extends Action {
    protected transient Pin resultPin = new Pin(new PinInteger(), R.string.pin_int, true);
    protected transient Pin firstPin = new Pin(new PinInteger(), R.string.pin_int);
    protected transient Pin secondPin = new Pin(new PinInteger(), R.string.pin_int);

    public IntAction(ActionType type) {
        super(type);
        resultPin = addPin(resultPin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
    }

    public IntAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    public abstract void calculate(TaskRunnable runnable, FunctionContext context, Pin pin);
}
