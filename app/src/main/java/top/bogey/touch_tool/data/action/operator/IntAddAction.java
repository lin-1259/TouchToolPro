package top.bogey.touch_tool.data.action.operator;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class IntAddAction extends CalculateAction {
    private transient Pin outValuePin = new Pin(new PinInteger(), PinDirection.OUT);
    private transient Pin firstPin = new Pin(new PinInteger());
    private transient Pin secondPin = new Pin(new PinInteger());
    private final transient Pin executePin = new Pin(new PinInteger());
    private transient Pin addPin = new Pin(new PinAdd(executePin), R.string.action_subtitle_add_pin);

    public IntAddAction() {
        super(R.string.action_int_add_operator_title);
        outValuePin = addPin(outValuePin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public IntAddAction(JsonObject jsonObject) {
        super(R.string.action_int_add_operator_title, jsonObject);
        outValuePin = reAddPin(outValuePin);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
        reAddPin(executePin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinInteger value = (PinInteger) outValuePin.getValue();
        value.setValue(0);

        ArrayList<Pin> pins = getPins();
        int i = pins.indexOf(firstPin);
        for (; i < pins.size() - 1; i++) {
            Pin pinObject = pins.get(i);
            PinInteger result = (PinInteger) getPinValue(runnable, actionContext, pinObject);
            value.setValue(value.getValue() + result.getValue());
        }
    }
}
