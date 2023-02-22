package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class FunctionAction extends BaseAction {
    private final BaseFunction.FUNCTION_TAG tag;

    private transient BaseFunction baseFunction;
    private transient final Pin executePin;

    public FunctionAction(Context context, BaseFunction.FUNCTION_TAG tag, BaseFunction baseFunction) {
        super(context, tag.isStart() ? R.string.function_start : R.string.function_end);
        this.tag = tag;
        if (tag.isStart()) {
            executePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT, PinSlotType.SINGLE));
        } else {
            executePin = addPin(new Pin(new PinExecute(), PinSlotType.MULTI));
        }
        this.baseFunction = baseFunction;
    }

    public FunctionAction(JsonObject jsonObject) {
        super(jsonObject);
        tag = BaseFunction.FUNCTION_TAG.valueOf(jsonObject.get("tag").getAsString());
        executePin = addPin(tmpPins.remove(0));
        for (Pin pin : tmpPins) {
            addPin(pin);
        }
    }

    public void setBaseFunction(BaseFunction baseFunction) {
        this.baseFunction = baseFunction;
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
        if (tag.isStart()) return ((BaseFunction) actionContext).getInnerPinValue(runnable, pin);
        else return super.getPinValue(runnable, actionContext, pin);
    }

    @Override
    public String getDes() {
        return baseFunction.getTitle();
    }

    @Override
    public void setTitle(String des) {
        if (tag.isStart()) baseFunction.setTitle(des);
    }

    @Override
    public Pin addPin(Pin pin) {
        if (baseFunction != null) baseFunction.addInnerPin(pin);
        return super.addPin(pin);
    }

    @Override
    public Pin removePin(Pin pin) {
        if (baseFunction != null) baseFunction.removeInnerPin(pin);
        return super.removePin(pin);
    }

    public void setPinValue(Pin pin, PinObject value) {
        pin.setValue(value);
        baseFunction.setInnerPinValue(pin);
    }

    public void setPinTitle(Pin pin, String title) {
        pin.setTitle(title);
        baseFunction.setInnerPinTitle(pin);
    }

    public BaseFunction.FUNCTION_TAG getTag() {
        return tag;
    }

    public Pin getExecutePin() {
        return executePin;
    }
}
