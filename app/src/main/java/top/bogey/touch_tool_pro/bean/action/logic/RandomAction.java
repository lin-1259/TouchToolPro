package top.bogey.touch_tool_pro.bean.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class RandomAction extends NormalAction {
    private transient Pin secondPin = new Pin(new PinExecute(), R.string.pin_execute, true);
    protected transient Pin timesPin = new Pin(new PinInteger(1), R.string.action_random_logic_subtitle_times);
    protected transient Pin repeatPin = new Pin(new PinBoolean(false), R.string.action_random_logic_subtitle_repeat);
    private final transient Pin morePin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private transient Pin addPin = new Pin(new PinAdd(morePin, 2), R.string.action_subtitle_add_execute, true);
    private final transient ArrayList<Pin> executePins = new ArrayList<>();
    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_complete, true);

    public RandomAction() {
        super(ActionType.LOGIC_RANDOM);
        secondPin = addPin(secondPin);
        timesPin = addPin(timesPin);
        repeatPin = addPin(repeatPin);
        addPin = addPin(addPin);
        completePin = addPin(completePin);
    }

    public RandomAction(JsonObject jsonObject) {
        super(jsonObject);
        executePins.add(outPin);
        executePins.add(secondPin = reAddPin(secondPin));
        timesPin = reAddPin(timesPin);
        repeatPin = reAddPin(repeatPin);
        executePins.addAll(reAddPin(morePin, 2));
        addPin = reAddPin(addPin);
        completePin = reAddPin(completePin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger times = (PinInteger) getPinValue(runnable, context, timesPin);
        PinBoolean repeat = (PinBoolean) getPinValue(runnable, context, repeatPin);
        ArrayList<Pin> pins = new ArrayList<>(executePins);
        for (int i = 0; i < times.getValue(); i++) {
            if (runnable.isInterrupt() || context.isEnd()) return;
            if (pins.isEmpty()) break;
            int index = (int) Math.round(Math.random() * (pins.size() - 1));
            if (repeat.isBool()) {
                executeNext(runnable, context, pins.get(index));
            } else {
                executeNext(runnable, context, pins.remove(index));
            }
        }
        executeNext(runnable, context, completePin);
    }
}
