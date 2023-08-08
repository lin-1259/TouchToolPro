package top.bogey.touch_tool_pro.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class WaitIfLogicAction extends NormalAction {
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin conditionPin = new Pin(new PinBoolean(false), R.string.pin_boolean);
    private transient Pin timeoutPin = new Pin(new PinInteger(1000), R.string.action_wait_condition_logic_subtitle_timeout);
    private transient Pin periodicPin = new Pin(new PinInteger(100), R.string.action_wait_condition_logic_subtitle_periodic);

    public WaitIfLogicAction() {
        super(ActionType.LOGIC_WAIT_IF);
        falsePin = addPin(falsePin);
        conditionPin = addPin(conditionPin);
        timeoutPin = addPin(timeoutPin);
        periodicPin = addPin(periodicPin);
    }

    public WaitIfLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        falsePin = reAddPin(falsePin);
        conditionPin = reAddPin(conditionPin);
        timeoutPin = reAddPin(timeoutPin);
        periodicPin = reAddPin(periodicPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean condition = (PinBoolean) getPinValue(runnable, context, conditionPin);
        PinInteger timeout = (PinInteger) getPinValue(runnable, context, timeoutPin);
        PinInteger periodic = (PinInteger) getPinValue(runnable, context, periodicPin);

        long startTime = System.currentTimeMillis();
        while (!condition.isBool()) {
            runnable.sleep(periodic.getValue());
            if (runnable.isInterrupt() || context.isEnd()) return;
            if (timeout.getValue() < System.currentTimeMillis() - startTime) break;
            condition = (PinBoolean) getPinValue(runnable, context, conditionPin);
        }

        if (condition.isBool()) {
            executeNext(runnable, context, outPin);
        } else {
            executeNext(runnable, context, falsePin);
        }
    }

    public Pin getConditionPin() {
        return conditionPin;
    }
}
