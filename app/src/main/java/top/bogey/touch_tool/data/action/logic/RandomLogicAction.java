package top.bogey.touch_tool.data.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class RandomLogicAction extends NormalAction {
    private transient Pin timesPin = new Pin(new PinInteger(1), R.string.action_random_logic_subtitle_times);
    private transient Pin secondExcutePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    private final transient Pin executePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    private transient Pin addPin = new Pin(new PinAdd(executePin, 2), R.string.action_subtitle_add_pin, PinDirection.OUT);
    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_random_logic_subtitle_complete, PinDirection.OUT);

    public RandomLogicAction() {
        super(R.string.action_random_logic_title);
        timesPin = addPin(timesPin);
        secondExcutePin = addPin(secondExcutePin);
        addPin = addPin(addPin);
        completePin = addPin(completePin);
    }

    public RandomLogicAction(JsonObject jsonObject) {
        super(R.string.action_random_logic_title, jsonObject);
        timesPin = reAddPin(timesPin);
        secondExcutePin = reAddPin(secondExcutePin);
        reAddPin(executePin, 2);
        addPin = reAddPin(addPin);
        completePin = reAddPin(completePin);
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
