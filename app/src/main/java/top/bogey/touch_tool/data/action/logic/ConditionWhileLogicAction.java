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
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class ConditionWhileLogicAction extends NormalAction {
    private transient Pin conditionPin = new Pin(new PinBoolean(false), R.string.action_condition_while_logic_subtitle_condition);
    private transient Pin timeOutPin = new Pin(new PinInteger(5000), R.string.action_condition_while_logic_subtitle_timeout);
    private transient Pin endPin = new Pin(new PinExecute(), R.string.action_condition_while_logic_subtitle_end, PinDirection.OUT);

    public ConditionWhileLogicAction() {
        super(R.string.action_condition_while_logic_title);
        conditionPin = addPin(conditionPin);
        timeOutPin = addPin(timeOutPin);
        endPin = addPin(endPin);
    }

    public ConditionWhileLogicAction(JsonObject jsonObject) {
        super(R.string.action_condition_while_logic_title, jsonObject);
        conditionPin = reAddPin(conditionPin);
        timeOutPin = reAddPin(timeOutPin);
        endPin = reAddPin(endPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean condition = (PinBoolean) getPinValue(runnable, actionContext, conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(runnable, actionContext, timeOutPin);

        long startTime = System.currentTimeMillis();
        while (condition.getValue()) {
            if (runnable.isInterrupt() || actionContext.isReturned()) return;
            doNextAction(runnable, actionContext, outPin);
            if (timeout.getValue() < System.currentTimeMillis() - startTime) break;
            condition = (PinBoolean) getPinValue(runnable, actionContext, conditionPin);
        }
        doNextAction(runnable, actionContext, endPin);
    }
}
