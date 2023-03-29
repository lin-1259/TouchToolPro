package top.bogey.touch_tool.data.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;

public class ConditionLogicAction extends NormalAction {
    private transient Pin conditionPin = new Pin(new PinBoolean(false), R.string.action_condition_logic_subtitle_condition);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, PinDirection.OUT);

    public ConditionLogicAction() {
        super(R.string.action_condition_logic_title);
        conditionPin = addPin(conditionPin);
        falsePin = addPin(falsePin);
    }

    public ConditionLogicAction(JsonObject jsonObject) {
        super(R.string.action_condition_logic_title, jsonObject);
        conditionPin = reAddPin(conditionPin);
        falsePin = reAddPin(falsePin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean condition = (PinBoolean) getPinValue(runnable, actionContext, conditionPin);
        if (condition.getValue()) {
            doNextAction(runnable, actionContext, outPin);
        } else {
            doNextAction(runnable, actionContext, falsePin);
        }
    }
}
