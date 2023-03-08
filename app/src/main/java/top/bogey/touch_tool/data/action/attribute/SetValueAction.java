package top.bogey.touch_tool.data.action.attribute;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class SetValueAction extends NormalAction {
    private transient final Pin valuePin;
    private final String key;

    public SetValueAction(Context context, String key, PinObject value) {
        super(context, R.string.action_set_value_action_title);
        this.key = key;
        valuePin = addPin(new Pin(value, key));
    }

    public SetValueAction(JsonObject jsonObject) {
        super(jsonObject);
        key = jsonObject.get("key").getAsString();
        valuePin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinObject value = getPinValue(runnable, actionContext, valuePin);
        actionContext.addAttr(key, value);
        doNextAction(runnable, actionContext, outPin);
    }

    public String getKey() {
        return key;
    }

    public void setValue(PinObject value) {
        valuePin.setValue(value);
    }
}
