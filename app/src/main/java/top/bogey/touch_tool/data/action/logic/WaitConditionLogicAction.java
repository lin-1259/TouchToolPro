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

public class WaitConditionLogicAction extends NormalAction {
    private transient Pin conditionPin = new Pin(new PinBoolean(false), R.string.action_condition_logic_subtitle_condition);
    private transient Pin timeOutPin = new Pin(new PinInteger(1000), R.string.action_wait_condition_logic_subtitle_timeout);
    private transient Pin periodicPin = new Pin(new PinInteger(100), R.string.action_wait_condition_logic_subtitle_periodic);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, PinDirection.OUT);

    public WaitConditionLogicAction() {
        super(R.string.action_wait_condition_logic_title);
        conditionPin = addPin(conditionPin);
        timeOutPin = addPin(timeOutPin);
        periodicPin = addPin(periodicPin);
        falsePin = addPin(falsePin);
    }

    public WaitConditionLogicAction(JsonObject jsonObject) {
        super(R.string.action_wait_condition_logic_title, jsonObject);
        conditionPin = reAddPin(conditionPin);
        timeOutPin = reAddPin(timeOutPin);
        periodicPin = reAddPin(periodicPin);
        falsePin = reAddPin(falsePin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean condition = (PinBoolean) getPinValue(runnable, actionContext, conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(runnable, actionContext, timeOutPin);
        PinInteger periodic = (PinInteger) getPinValue(runnable, actionContext, periodicPin);
        long startTime = System.currentTimeMillis();
        while (!condition.getValue()) {
            sleep(periodic.getValue());
            if (runnable.isInterrupt() || actionContext.isReturned()) return;
            if (timeout.getValue() < System.currentTimeMillis() - startTime) break;
            condition = (PinBoolean) getPinValue(runnable, actionContext, conditionPin);
        }

        if (condition.getValue()) {
            doNextAction(runnable, actionContext, outPin);
        } else {
            doNextAction(runnable, actionContext, falsePin);
        }
    }

    public Pin getConditionPin() {
        return conditionPin;
    }
}
