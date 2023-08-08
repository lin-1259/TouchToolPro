package top.bogey.touch_tool_pro.bean.action.function;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class FunctionEndAction extends FunctionInnerAction {

    public FunctionEndAction(Function owner) {
        super(ActionType.CUSTOM_END, owner);
        setX(1);
        setY(21);
    }

    public FunctionEndAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Function function = (Function) context;
        function.setEndAction(this);
        function.executeNext(runnable, context, pin);
    }

    @Override
    public Pin addPin(Pin pin) {
        pin.setOut(false);
        return super.addPin(pin);
    }

    @Override
    public void onPinAdded(Pin pin) {
        if (pin.isOut()) addPin((Pin) pin.copy());
    }
}
