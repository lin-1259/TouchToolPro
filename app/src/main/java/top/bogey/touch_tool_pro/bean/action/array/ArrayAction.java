package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;

public abstract class ArrayAction extends Action {

    public ArrayAction(ActionType type) {
        super(type);
    }

    public ArrayAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    public void setValueType(FunctionContext context, PinType type) {
    }

    protected PinValue createPinValue(PinType pinType) {
        try {
            Class<? extends PinObject> objectClass = pinType.getPinObjectClass();
            if (objectClass == null) return null;
            Constructor<? extends PinObject> constructor = objectClass.getConstructor();
            return (PinValue) constructor.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract PinType getPinType();
}
