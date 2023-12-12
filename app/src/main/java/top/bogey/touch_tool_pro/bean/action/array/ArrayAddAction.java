package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayAddAction extends ArrayNormalAction {
    private transient Pin valuePin = new Pin(new PinString(), R.string.action_array_subtitle_element);

    public ArrayAddAction() {
        super(ActionType.ARRAY_ADD);
        valuePin = addPin(valuePin);
    }

    public ArrayAddAction(JsonObject jsonObject) {
        super(jsonObject);
        valuePin = reAddPin(valuePin, getPinType());
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        PinValue value = (PinValue) getPinValue(runnable, context, valuePin);
        ArrayList<PinValue> values = array.getValues();
        values.add(value);
        executeNext(runnable, context, outPin);
    }

    @Override
    public void setValueType(FunctionContext context, PinType type) {
        super.setValueType(context, type);
        valuePin.setValue(createPinValue(type));
        valuePin.cleanLinks(context);
    }
}
