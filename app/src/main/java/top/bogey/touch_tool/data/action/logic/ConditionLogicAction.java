package top.bogey.touch_tool.data.action.logic;

import android.content.Context;
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

    public ConditionLogicAction(Context context) {
        super(context, R.string.action_condition_logic_title);
        addPin(inPin);
        conditionPin = addPin(new Pin<>(new PinBoolean(false), context.getString(R.string.action_condition_logic_subtitle_condition)));
        truePin = addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_condition_logic_subtitle_true), PinDirection.OUT));
        falsePin = addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_condition_logic_subtitle_false), PinDirection.OUT));
    }

    public ConditionLogicAction(Parcel in) {
        super(in);
        inPin = addPin(pinsTmp.remove(0));
        conditionPin = addPin(pinsTmp.remove(0));
        truePin = addPin(pinsTmp.remove(0));
        falsePin = addPin(pinsTmp.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        if (condition.getValue()) {
            super.doAction(worldState, runnable, truePin);
        } else {
            super.doAction(worldState, runnable, falsePin);
        }
    }
}
