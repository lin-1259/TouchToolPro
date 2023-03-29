package top.bogey.touch_tool.data.action.logic;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class ForLoopLogicAction extends NormalAction {
    private transient Pin startPin = new Pin(new PinInteger(1), R.string.action_for_loop_logic_subtitle_start);
    private transient Pin endPin = new Pin(new PinInteger(5), R.string.action_for_loop_logic_subtitle_end);
    private transient Pin currentPin = new Pin(new PinInteger(), R.string.action_for_loop_logic_subtitle_curr, PinDirection.OUT);
    private transient Pin completePin = new Pin(new PinExecute(), R.string.action_for_loop_logic_subtitle_complete, PinDirection.OUT);
    private transient Pin breakPin = new Pin(new PinExecute(), R.string.action_for_loop_logic_subtitle_break);

    private transient boolean needBreak = false;

    public ForLoopLogicAction() {
        super(R.string.action_for_loop_logic_title);
        startPin = addPin(startPin);
        endPin = addPin(endPin);
        currentPin = addPin(currentPin);
        completePin = addPin(completePin);
        breakPin = addPin(breakPin);
    }

    public ForLoopLogicAction(JsonObject jsonObject) {
        super(R.string.action_for_loop_logic_title, jsonObject);
        startPin = reAddPin(startPin);
        endPin = reAddPin(endPin);
        currentPin = reAddPin(currentPin);
        completePin = reAddPin(completePin);
        breakPin = reAddPin(breakPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (pin.getId().equals(inPin.getId())) {
            needBreak = false;
            PinInteger start = (PinInteger) getPinValue(runnable, actionContext, startPin);
            PinInteger end = (PinInteger) getPinValue(runnable, actionContext, endPin);
            PinInteger current = (PinInteger) currentPin.getValue();
            for (int i = start.getValue(); i <= end.getValue(); i++) {
                if (runnable.isInterrupt() || actionContext.isReturned()) return;
                if (needBreak) break;
                current.setValue(i);
                doNextAction(runnable, actionContext, outPin);
            }
            doNextAction(runnable, actionContext, completePin);
        } else {
            needBreak = true;
        }
    }
}
