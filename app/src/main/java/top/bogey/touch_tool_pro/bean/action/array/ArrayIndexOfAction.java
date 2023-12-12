package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayIndexOfAction extends ArrayWithAction {
    private transient Pin indexPin = new Pin(new PinInteger(), R.string.pin_index, true);
    private transient Pin valuePin = new Pin(new PinString(), R.string.action_array_subtitle_element);

    public ArrayIndexOfAction() {
        super(ActionType.ARRAY_INDEX_OF);
        indexPin = addPin(indexPin);
        valuePin = addPin(valuePin);
    }

    public ArrayIndexOfAction(JsonObject jsonObject) {
        super(jsonObject);
        indexPin = reAddPin(indexPin);
        valuePin = reAddPin(valuePin, getPinType());
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        ArrayList<PinValue> values = array.getValues();
        if (values.contains(value)) {
            indexPin.getValue(PinInteger.class).setValue(values.indexOf(value) + 1);
        } else {
            indexPin.getValue(PinInteger.class).setValue(0);
        }
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        super.setValueType(context, type);
        valuePin.setValue(createPinValue(type));
        valuePin.cleanLinks(context);
    }
}
