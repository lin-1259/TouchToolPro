package top.bogey.touch_tool.data.action.logic;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class WaitConditionLogicAction extends BaseAction {
    private final Pin<? extends PinObject> conditionPin;
    private final Pin<? extends PinObject> timeOutPin;
    private final Pin<? extends PinObject> periodicPin;
    private final Pin<? extends PinObject> truePin;
    private final Pin<? extends PinObject> falsePin;

    public WaitConditionLogicAction() {
        super();
        addPin(inPin);
        conditionPin = addPin(new Pin<>(new PinBoolean(false), R.string.action_condition_logic_subtitle_condition));
        timeOutPin = addPin(new Pin<>(new PinInteger(1000), R.string.action_wait_condition_logic_subtitle_timeout));
        periodicPin = addPin(new Pin<>(new PinInteger(100), R.string.action_wait_condition_logic_subtitle_periodic));
        truePin = addPin(new Pin<>(new PinExecute(), R.string.action_condition_logic_subtitle_true, PinDirection.OUT));
        falsePin = addPin(new Pin<>(new PinExecute(), R.string.action_condition_logic_subtitle_false, PinDirection.OUT));
        titleId = R.string.action_wait_condition_logic_title;
    }

    public WaitConditionLogicAction(Parcel in) {
        super(in);
        inPin = addPin(pinsTmp.remove(0));
        conditionPin = addPin(pinsTmp.remove(0));
        timeOutPin = addPin(pinsTmp.remove(0));
        periodicPin = addPin(pinsTmp.remove(0));
        truePin = addPin(pinsTmp.remove(0));
        falsePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_wait_condition_logic_title;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(worldState, runnable.getTask(), timeOutPin);
        PinInteger periodic = (PinInteger) getPinValue(worldState, runnable.getTask(), periodicPin);
        long startTime = System.currentTimeMillis();
        while (!condition.getValue()) {
            sleep(periodic.getValue());
            if (runnable.isInterrupt()) return;
            if (timeout.getValue() < System.currentTimeMillis() - startTime) break;
            condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        }

        if (condition.getValue()) {
            super.doAction(worldState, runnable, truePin);
        } else {
            super.doAction(worldState, runnable, falsePin);
        }
    }
}
