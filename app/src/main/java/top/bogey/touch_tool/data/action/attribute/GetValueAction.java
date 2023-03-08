package top.bogey.touch_tool.data.action.attribute;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class GetValueAction extends CalculateAction {
    private transient final Pin valuePin;
    private final String key;

    public GetValueAction(Context context, String key, PinObject value) {
        super(context, R.string.action_get_value_action_title);
        this.key = key;
        valuePin = addPin(new Pin(value, key, PinDirection.OUT));
    }

    public GetValueAction(JsonObject jsonObject) {
        super(jsonObject);
        key = jsonObject.get("key").getAsString();
        valuePin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        valuePin.setValue(actionContext.getAttr(key));
    }

    public String getKey() {
        return key;
    }

    public void setValue(PinObject value) {
        valuePin.setValue(value);
    }
}
