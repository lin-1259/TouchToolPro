package top.bogey.touch_tool.data.action.logic;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class ConditionWhileLogicAction extends NormalAction {
    private final Pin<? extends PinObject> conditionPin;
    private final Pin<? extends PinObject> endPin;

    public ConditionWhileLogicAction() {
        super();
        conditionPin = addPin(new Pin<>(new PinBoolean(false), R.string.action_condition_while_logic_subtitle_condition));
        endPin = addPin(new Pin<>(new PinExecute(), R.string.action_condition_while_logic_subtitle_end, PinDirection.OUT));
        titleId = R.string.action_condition_while_logic_title;
    }

    public ConditionWhileLogicAction(Parcel in) {
        super(in);
        conditionPin = addPin(pinsTmp.remove(0));
        endPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_condition_while_logic_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        while (condition.getValue()) {
            doAction(worldState, runnable, outPin);
        }
        doAction(worldState, runnable, endPin);
    }
}
