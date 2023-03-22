package top.bogey.touch_tool.data.action.logic;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class RandomLogicAction extends NormalAction {
    private transient Pin timesPin;
    private transient Pin completePin;

    public RandomLogicAction(Context context) {
        super(context, R.string.action_random_logic_title);
        timesPin = addPin(new Pin(new PinInteger(1), context.getString(R.string.action_random_logic_subtitle_times)));
        addPin(new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT));
        Pin executePin = new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT);
        addPin(new Pin(new PinAdd(executePin, 2), context.getString(R.string.action_subtitle_add_pin), PinDirection.OUT, PinSlotType.EMPTY));
        completePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_random_logic_subtitle_complete), PinDirection.OUT));
    }

    public RandomLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        Pin lastPin = tmpPins.get(tmpPins.size() - 1);
        if (lastPin.getValue() instanceof PinExecute) {
            timesPin = addPin(tmpPins.remove(0));
            completePin = addPin(tmpPins.remove(tmpPins.size() - 1));
        }

        for (Pin pin : tmpPins) {
            if (pin.getDirection().isOut()) addPin(pin);
        }
        tmpPins.clear();
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        ArrayList<Pin> pins = getShowPins();
        pins.remove(inPin);
        pins.remove(pins.size() - 1);

        if (timesPin == null) {
            int round = (int) Math.round(Math.random() * (pins.size() - 1));
            Pin executePin = pins.get(round);
            doNextAction(runnable, actionContext, executePin);
        } else {
            pins.remove(timesPin);
            pins.remove(completePin);
            int times = ((PinInteger) getPinValue(runnable, actionContext, timesPin)).getValue();
            times = Math.min(times, pins.size());
            for (int i = 0; i < times; i++) {
                if (runnable.isInterrupt() || actionContext.isReturned()) return;
                int round = (int) Math.round(Math.random() * (pins.size() - 1));
                Pin executePin = pins.get(round);
                doNextAction(runnable, actionContext, executePin);
                pins.remove(round);
            }
            doNextAction(runnable, actionContext, completePin);
        }
    }
}
