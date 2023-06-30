package top.bogey.touch_tool.data.action.logic;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;

public class SequenceLogicAction extends NormalAction {
    private transient Pin secondExcutePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    private final transient Pin executePin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    private transient Pin addPin = new Pin(new PinAdd(executePin), R.string.action_subtitle_add_execute, PinDirection.OUT);

    public SequenceLogicAction() {
        super(R.string.action_sequence_logic_title);
        secondExcutePin = addPin(secondExcutePin);
        addPin = addPin(addPin);
    }

    public SequenceLogicAction(JsonObject jsonObject) {
        super(R.string.action_sequence_logic_title, jsonObject);
        secondExcutePin = reAddPin(secondExcutePin);
        reAddPin(executePin, 1);
        addPin = reAddPin(addPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        ArrayList<Pin> pins = getPins();
        for (int i = pins.indexOf(outPin); i < pins.size() - 1; i++) {
            if (runnable.isInterrupt() || actionContext.isReturned()) return;
            doNextAction(runnable, actionContext, pins.get(i));
        }
    }
}
