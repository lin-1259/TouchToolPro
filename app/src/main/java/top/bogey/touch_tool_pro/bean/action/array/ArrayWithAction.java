package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;

public class ArrayWithAction extends ArrayAction {
    protected transient Pin arrayPin = new Pin(new PinValueArray(PinType.STRING), R.string.pin_value_array);

    public ArrayWithAction(ActionType type) {
        super(type);
        arrayPin = addPin(arrayPin);
    }

    public ArrayWithAction(JsonObject jsonObject) {
        super(jsonObject);
        arrayPin = reAddPin(arrayPin);
    }

    public void setValueType(FunctionContext context, PinType type) {
        arrayPin.getValue(PinValueArray.class).setPinType(type);
        arrayPin.cleanLinks(context);
    }

    @Override
    protected PinType getPinType() {
        return arrayPin.getValue(PinValueArray.class).getPinType();
    }
}
