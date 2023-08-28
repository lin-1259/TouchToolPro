package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class SetVariableValue extends NormalAction {
    private final String varKey;
    private final transient Pin valuePin;

    public SetVariableValue(String varKey, PinValue value) {
        super(ActionType.VAR_SET);
        this.varKey = varKey;
        valuePin = addPin(new Pin(value));
        valuePin.setTitle(varKey);
    }

    public SetVariableValue(JsonObject jsonObject) {
        super(jsonObject);
        varKey = GsonUtils.getAsString(jsonObject, "varKey", null);
        valuePin = addPin(tmpPins.remove(0));
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (isError(context)) return;
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        context.setVarOnParent(varKey, (PinValue) value.copy());
        executeNext(runnable, context, outPin);
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        PinValue value = context.findVar(varKey);
        if (value == null) return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_variable_action_tips);
        return super.check(context);
    }

    public String getVarKey() {
        return varKey;
    }

    public void setValue(PinValue value) {
        valuePin.setValue(value);
    }
}
