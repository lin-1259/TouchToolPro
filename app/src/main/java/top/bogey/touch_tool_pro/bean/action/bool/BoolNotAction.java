package top.bogey.touch_tool_pro.bean.action.bool;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.check.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class BoolNotAction extends CheckAction {
    private transient Pin boolPin = new Pin(new PinBoolean(), R.string.pin_boolean);

    public BoolNotAction() {
        super(ActionType.BOOL_NOT);
        boolPin = addPin(boolPin);
    }

    public BoolNotAction(JsonObject jsonObject) {
        super(jsonObject);
        boolPin = reAddPin(boolPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);
        PinBoolean bool = (PinBoolean) getPinValue(runnable, context, boolPin);
        result.setBool(!bool.isBool());
    }
}
