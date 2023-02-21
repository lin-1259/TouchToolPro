package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class FunctionAction extends NormalAction {
    private final BaseFunction.FUNCTION_TAG tag;

    private transient BaseFunction baseFunction;

    public FunctionAction(Context context, BaseFunction.FUNCTION_TAG tag) {
        super(context, 0);
        this.tag = tag;
    }

    public FunctionAction(JsonObject jsonObject) {
        super(jsonObject);
        tag = BaseFunction.FUNCTION_TAG.valueOf(jsonObject.get("tag").getAsString());
        for (Pin pin : tmpPins) {
            addPin(pin);
        }
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart()) super.doAction(runnable, actionContext, pin);
        else {
            ((BaseFunction) actionContext).setEndFunctionAction(this);
        }
    }

    @Override
    protected PinObject getPinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart()) return ((BaseFunction) actionContext).getPinValue(runnable, pin);
        else return super.getPinValue(runnable, actionContext, pin);
    }

    @Override
    public String getTitle() {
        if (tag.isStart()) return baseFunction.getDes();
        else return null;
    }

    public BaseFunction.FUNCTION_TAG getTag() {
        return tag;
    }
}
