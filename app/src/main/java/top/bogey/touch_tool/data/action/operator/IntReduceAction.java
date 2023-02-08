package top.bogey.touch_tool.data.action.operator;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntReduceAction extends IntDivAction {

    public IntReduceAction(Context context) {
        super(context, R.string.action_int_reduce_operator_title);
    }

    public IntReduceAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<?> pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) outValuePin.getValue();

        PinInteger origin = (PinInteger) getPinValue(worldState, task, originPin);
        PinInteger second = (PinInteger) getPinValue(worldState, task, secondPin);
        value.setValue(origin.getValue() - second.getValue());
    }
}
