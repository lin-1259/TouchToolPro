package top.bogey.touch_tool.data.action.operator;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class IntMultiAction extends IntDivAction {

    public IntMultiAction() {
        super();
        titleId = R.string.action_int_multi_operator_title;
    }

    public IntMultiAction(Parcel in) {
        super(in);
        titleId = R.string.action_int_multi_operator_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) getPinValue(worldState, task, outValuePin);

        PinInteger origin = (PinInteger) getPinValue(worldState, task, originPin);
        PinInteger second = (PinInteger) getPinValue(worldState, task, secondPin);
        value.setValue(origin.getValue() * second.getValue());
    }
}
