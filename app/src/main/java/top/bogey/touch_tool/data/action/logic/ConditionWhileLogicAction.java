package top.bogey.touch_tool.data.action.logic;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class ConditionWhileLogicAction extends NormalAction {
    private transient final Pin<?> conditionPin;
    private transient final Pin<?> timeOutPin;
    private transient final Pin<?> endPin;

    public ConditionWhileLogicAction(Context context) {
        super(context, R.string.action_condition_while_logic_title);
        conditionPin = addPin(new Pin<>(new PinBoolean(false), context.getString(R.string.action_condition_while_logic_subtitle_condition)));
        timeOutPin = addPin(new Pin<>(new PinInteger(5000), context.getString(R.string.action_condition_while_logic_subtitle_timeout)));
        endPin = addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_condition_while_logic_subtitle_end), PinDirection.OUT));
    }

    public ConditionWhileLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        conditionPin = addPin(tmpPins.remove(0));
        timeOutPin = addPin(tmpPins.remove(0));
        endPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<?> pin) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(worldState, runnable.getTask(), timeOutPin);

        long startTime = System.currentTimeMillis();
        while (condition.getValue()) {
            if (runnable.isInterrupt()) return;
            super.doAction(worldState, runnable, outPin);
            if (timeout.getValue() < System.currentTimeMillis() - startTime) break;
            condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        }
        super.doAction(worldState, runnable, endPin);
    }
}
