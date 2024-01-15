package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ArrayValidIndexAction extends ArrayWithAction {
    private transient Pin resultPin = new Pin(new PinBoolean(), R.string.action_check_subtitle_result, true);
    private transient Pin indexPin = new Pin(new PinInteger(1), R.string.pin_index);

    public ArrayValidIndexAction() {
        super(ActionType.ARRAY_VALID_INDEX);
        resultPin = addPin(resultPin);
        indexPin = addPin(indexPin);
    }

    public ArrayValidIndexAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        indexPin = reAddPin(indexPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArray array = (PinValueArray) getPinValue(runnable, context, arrayPin);
        PinInteger index = (PinInteger) getPinValue(runnable, context, indexPin);
        ArrayList<PinValue> values = array.getValues();
        resultPin.getValue(PinBoolean.class).setBool(index.getValue() > 0 && index.getValue() <= values.size());
    }
}
