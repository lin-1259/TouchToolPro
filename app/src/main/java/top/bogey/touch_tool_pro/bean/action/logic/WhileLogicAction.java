package top.bogey.touch_tool_pro.bean.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class WhileLogicAction extends NormalAction {
    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_complete, true);
    private transient Pin breakPin = new Pin(new PinExecute(), R.string.action_condition_while_logic_subtitle_break);
    private transient Pin conditionPin = new Pin(new PinBoolean(false), R.string.pin_boolean);
    private transient boolean needBreak;

    public WhileLogicAction() {
        super(ActionType.LOGIC_WHILE);
        completePin = addPin(completePin);
        breakPin = addPin(breakPin);
        conditionPin = addPin(conditionPin);
        needBreak = false;
    }

    public WhileLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        completePin = reAddPin(completePin);
        breakPin = reAddPin(breakPin);
        conditionPin = reAddPin(conditionPin);
        needBreak = false;
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (pin.equals(inPin)) {
            needBreak = false;
            PinBoolean condition = (PinBoolean) getPinValue(runnable, context, conditionPin);
            while (condition.isBool()) {
                if (runnable.isInterrupt() || context.isEnd()) return;
                executeNext(runnable, context, outPin);
                if (needBreak) break;
                condition = (PinBoolean) getPinValue(runnable, context, conditionPin);
            }
            executeNext(runnable, context, completePin);
        } else {
            needBreak = true;
        }
    }
}
