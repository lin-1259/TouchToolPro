package top.bogey.touch_tool.data.action.attribute;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.utils.GsonUtils;

public class GetValueAction extends CalculateAction {
    private transient Pin valuePin = new Pin(new PinString(), PinDirection.OUT);
    private final String key;

    public GetValueAction(String key, PinObject value) {
        super(R.string.action_get_value_action_title);
        this.key = key;
        valuePin = addPin(new Pin(value, key, PinDirection.OUT));
    }

    public GetValueAction(JsonObject jsonObject) {
        super(R.string.action_get_value_action_title, jsonObject);
        key = GsonUtils.getAsString(jsonObject, "key", null);
        if (key == null) throw new RuntimeException("变量解析失败");
        valuePin = reAddPin(valuePin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinObject attr = actionContext.getAttr(key);
        ActionContext context = actionContext;
        while (attr == null) {
            context = context.getParent();
            if (context == null) break;
            attr = context.getAttr(key);
        }
        valuePin.setValue(attr);
    }

    public String getKey() {
        return key;
    }

    public void setValue(PinObject value) {
        valuePin.setValue(value);
    }
}
