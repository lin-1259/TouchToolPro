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

public class ConditionLogicAction extends NormalAction {
    private transient final Pin conditionPin;
    private transient final Pin falsePin;

    public ConditionLogicAction(Context context) {
        super(context, R.string.action_condition_logic_title);
        conditionPin = addPin(new Pin(new PinBoolean(false), context.getString(R.string.action_condition_logic_subtitle_condition)));
        falsePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_logic_subtitle_false), PinDirection.OUT));
    }

    public ConditionLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        conditionPin = addPin(tmpPins.remove(0));
        falsePin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin pin) {
        PinBoolean condition = (PinBoolean) getPinValue(worldState, runnable.getTask(), conditionPin);
        if (condition.getValue()) {
            super.doAction(worldState, runnable, outPin);
        } else {
            super.doAction(worldState, runnable, falsePin);
        }
    }
}
