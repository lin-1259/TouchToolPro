package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class FunctionAction extends BaseAction {
    private final BaseFunction.FUNCTION_TAG tag;

    private transient BaseFunction baseFunction;

    public FunctionAction(Context context, BaseFunction.FUNCTION_TAG tag) {
        super(context);
        this.tag = tag;
    }

    public FunctionAction(JsonObject jsonObject) {
        super(jsonObject);
        tag = BaseFunction.FUNCTION_TAG.valueOf(jsonObject.get("tag").getAsString());
        for (Pin pin : tmpPins) {
            addPin(pin);
        }
    }

    public void setBaseFunction(BaseFunction baseFunction) {
        this.baseFunction = baseFunction;
    }

    @Override
    protected void doNextAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart()) super.doNextAction(runnable, actionContext, pin);
        else baseFunction.doNextAction(runnable, actionContext, pin);
    }

    @Override
    protected PinObject getPinValue(ActionContext actionContext, Pin pin) {
        if (tag.isStart()) return baseFunction.getPinValue(actionContext, pin);
        else return super.getPinValue(actionContext, pin);
    }
}
