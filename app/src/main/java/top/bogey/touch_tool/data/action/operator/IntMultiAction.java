package top.bogey.touch_tool.data.action.operator;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntMultiAction extends IntDivAction {

    public IntMultiAction(Context context) {
        super(context, R.string.action_int_multi_operator_title);
    }

    public IntMultiAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) outValuePin.getValue();

        PinInteger origin = (PinInteger) getPinValue(worldState, task, originPin);
        PinInteger second = (PinInteger) getPinValue(worldState, task, secondPin);
        value.setValue(origin.getValue() * second.getValue());
    }
}
