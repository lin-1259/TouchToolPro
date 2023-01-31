package top.bogey.touch_tool.data.action.logic;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class ConditionWhileLogicAction extends NormalAction {
    private final Pin<? extends PinObject> conditionPin;
    private final Pin<? extends PinObject> timeOutPin;
    private final Pin<? extends PinObject> endPin;

    public ConditionWhileLogicAction() {
        super();
        conditionPin = addPin(new Pin<>(new PinBoolean(false), R.string.action_condition_while_logic_subtitle_condition));
        timeOutPin = addPin(new Pin<>(new PinInteger(5000), R.string.action_condition_while_logic_subtitle_timeout));
        endPin = addPin(new Pin<>(new PinExecute(), R.string.action_condition_while_logic_subtitle_end, PinDirection.OUT));
        titleId = R.string.action_condition_while_logic_title;
    }

    public ConditionWhileLogicAction(Parcel in) {
        super(in);
        conditionPin = addPin(pinsTmp.remove(0));
        timeOutPin = addPin(pinsTmp.remove(0));
        endPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_condition_while_logic_title;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(worldState, runnable.getTask(), timeOutPin);

        long startTime = System.currentTimeMillis();
        while (condition.getValue()) {
            super.doAction(worldState, runnable, outPin);
            if (timeout.getValue() < System.currentTimeMillis() - startTime) break;
        }
        super.doAction(worldState, runnable, endPin);
    }
}
