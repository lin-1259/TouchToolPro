package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArraySizeAction extends ArrayWithAction {
    private transient Pin countPin = new Pin(new PinInteger(), R.string.pin_int, true);

    public ArraySizeAction() {
        super(ActionType.ARRAY_SIZE);
        countPin = addPin(countPin);
    }

    public ArraySizeAction(JsonObject jsonObject) {
        super(jsonObject);
        countPin = reAddPin(countPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        ArrayList<PinValue> values = array.getValues();
        countPin.getValue(PinInteger.class).setValue(values.size());
    }
}
