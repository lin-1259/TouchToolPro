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
import top.bogey.touch_tool.data.pin.object.PinObject;

public class ConditionLogicAction extends BaseAction {
    private final Pin<? extends PinObject> conditionPin;
    private final Pin<? extends PinObject> truePin;
    private final Pin<? extends PinObject> falsePin;

    public ConditionLogicAction() {
        super();
        addPin(inPin);
        conditionPin = addPin(new Pin<>(new PinBoolean(false), R.string.action_condition_logic_subtitle_condition));
        truePin = addPin(new Pin<>(new PinExecute(), R.string.action_condition_logic_subtitle_true, PinDirection.OUT));
        falsePin = addPin(new Pin<>(new PinExecute(), R.string.action_condition_logic_subtitle_false, PinDirection.OUT));
        titleId = R.string.action_condition_logic_title;
    }

    public ConditionLogicAction(Parcel in) {
        super(in);
        inPin = addPin(pinsTmp.remove(0));
        conditionPin = addPin(pinsTmp.remove(0));
        truePin = addPin(pinsTmp.remove(0));
        falsePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_condition_logic_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        if (condition.getValue()) {
            doAction(worldState, runnable, truePin);
        } else {
            doAction(worldState, runnable, falsePin);
        }
    }
}
