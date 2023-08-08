package top.bogey.touch_tool_pro.bean.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.normal.NormalAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class SequenceAction extends NormalAction {
    private transient Pin secondPin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private final transient Pin morePin = new Pin(new PinExecute(), R.string.pin_execute, true);
    private transient Pin addPin = new Pin(new PinAdd(morePin), R.string.action_subtitle_add_execute, true);
    private final transient ArrayList<Pin> executePins = new ArrayList<>();

    public SequenceAction() {
        super(ActionType.LOGIC_SEQUENCE);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
    }

    public SequenceAction(JsonObject jsonObject) {
        super(jsonObject);
        executePins.add(outPin);
        executePins.add(secondPin = reAddPin(secondPin));
        executePins.addAll(reAddPin(morePin, 1));
        addPin = reAddPin(addPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        for (Pin executePin : executePins) {
            if (runnable.isInterrupt() || context.isEnd()) return;
            executeNext(runnable, context, executePin);
        }
    }
}
