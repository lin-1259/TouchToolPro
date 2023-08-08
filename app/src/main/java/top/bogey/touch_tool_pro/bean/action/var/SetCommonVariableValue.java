package top.bogey.touch_tool_pro.bean.action.var;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class SetCommonVariableValue extends NormalAction {
    private final String varKey;
    private final transient Pin valuePin;

    public SetCommonVariableValue(String varKey, PinValue value) {
        super(ActionType.COMMON_VAR_SET);
        this.varKey = varKey;
        valuePin = addPin(new Pin(value));
        valuePin.setTitle(varKey);
    }

    public SetCommonVariableValue(JsonObject jsonObject) {
        super(jsonObject);
        varKey = GsonUtils.getAsString(jsonObject, "varKey", null);
        valuePin = addPin(tmpPins.remove(0));
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!check(context)) return;
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        SaveRepository.getInstance().setVariable(varKey, value);
        executeNext(runnable, context, outPin);
    }

    @Override
    public boolean check(FunctionContext context) {
        PinValue value = SaveRepository.getInstance().getVariable(varKey);
        return value != null;
    }

    public String getVarKey() {
        return varKey;
    }

    public void setValue(PinValue value) {
        valuePin.setValue(value);
    }
}
