package top.bogey.touch_tool_pro.bean.action.function;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class FunctionStartAction extends FunctionInnerAction {

    public FunctionStartAction(Function owner) {
        super(ActionType.CUSTOM_START, owner);
        setX(1);
        setY(1);
    }

    public FunctionStartAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public Pin addPin(Pin pin) {
        pin.setOut(true);
        return super.addPin(pin);
    }

    @Override
    public PinObject getPinValue(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Function function = (Function) context;
        return function.getPinValue(runnable, context, pin);
    }

    @Override
    public void onPinAdded(Pin pin) {
        if (!pin.isOut()) addPin((Pin) pin.copy());
    }

}
