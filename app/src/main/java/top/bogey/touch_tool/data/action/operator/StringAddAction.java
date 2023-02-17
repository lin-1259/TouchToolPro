package top.bogey.touch_tool.data.action.operator;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinString;

public class StringAddAction extends CalculateAction {
    private transient final Pin outValuePin;
    private transient final Pin firstPin;

    public StringAddAction(Context context) {
        super(context, R.string.action_string_add_operator_title);
        outValuePin = addPin(new Pin(new PinString(), PinDirection.OUT, PinSlotType.MULTI));
        firstPin = addPin(new Pin(new PinString()));
        addPin(new Pin(new PinString()));
        Pin executePin = new Pin(new PinString());
        addPin(new Pin(new PinAdd(executePin), context.getString(R.string.action_subtitle_add_pin), PinSlotType.EMPTY));
    }

    public StringAddAction(JsonObject jsonObject) {
        super(jsonObject);
        outValuePin = addPin(tmpPins.remove(0));
        firstPin = addPin(tmpPins.remove(0));
        for (Pin pin : tmpPins) {
            addPin(pin);
        }
        tmpPins.clear();
    }

    @Override
    protected void calculatePinValue(ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinString value = (PinString) outValuePin.getValue();

        ArrayList<Pin> pins = getPins();
        int i = pins.indexOf(firstPin);
        StringBuilder builder = new StringBuilder();
        for (; i < pins.size() - 1; i++) {
            Pin pinObject = pins.get(i);
            PinString result = (PinString) getPinValue(actionContext, pinObject);
            builder.append(result.getValue());
        }
        value.setValue(builder.toString());
    }
}
