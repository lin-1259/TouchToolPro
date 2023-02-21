package top.bogey.touch_tool.data.action.logic;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;

public class SequenceLogicAction extends NormalAction {
    public SequenceLogicAction(Context context) {
        super(context, R.string.action_sequence_logic_title);
        addPin(new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT));
        Pin executePin = new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT);
        addPin(new Pin(new PinAdd(executePin), context.getString(R.string.action_subtitle_add_pin), PinDirection.OUT, PinSlotType.EMPTY));
    }

    public SequenceLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        for (Pin pin : tmpPins) {
            addPin(pin);
        }
        tmpPins.clear();
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
