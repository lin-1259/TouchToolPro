package top.bogey.touch_tool.data.action.operator;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class IntDivAction extends BaseAction {
    protected final Pin<? extends PinObject> outValuePin;
    protected final Pin<? extends PinObject> originPin;
    protected final Pin<? extends PinObject> secondPin;

    public IntDivAction() {
        super();
        outValuePin = addPin(new Pin<>(new PinInteger(), 0, PinDirection.OUT, PinSlotType.MULTI));
        originPin = addPin(new Pin<>(new PinInteger()));
        secondPin = addPin(new Pin<>(new PinInteger()));
        titleId = R.string.action_int_div_operator_title;
    }

    public IntDivAction(Parcel in) {
        super(in);
        outValuePin = addPin(pinsTmp.remove(0));
        originPin = addPin(pinsTmp.remove(0));
        secondPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_int_div_operator_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) getPinValue(worldState, task, outValuePin);

        PinInteger origin = (PinInteger) getPinValue(worldState, task, originPin);
        PinInteger second = (PinInteger) getPinValue(worldState, task, secondPin);
        int secondValue = second.getValue();
        if (secondValue == 0) secondValue = 1;
        value.setValue(origin.getValue() / secondValue);
    }
}
