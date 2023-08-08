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

public class IfLogicAction extends NormalAction {
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin conditionPin = new Pin(new PinBoolean(false), R.string.pin_boolean);

    public IfLogicAction() {
        super(ActionType.LOGIC_IF);
        falsePin = addPin(falsePin);
        conditionPin = addPin(conditionPin);
    }

    public IfLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        falsePin = reAddPin(falsePin);
        conditionPin = reAddPin(conditionPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean condition = (PinBoolean) getPinValue(runnable, context, conditionPin);
        if (condition.isBool()) {
            executeNext(runnable, context, outPin);
        } else {
            executeNext(runnable, context, falsePin);
        }
    }
}
