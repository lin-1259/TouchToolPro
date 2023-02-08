package top.bogey.touch_tool.data.action.logic;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class ForLoopLogicAction extends NormalAction {
    private transient final Pin<?> startPin;
    private transient final Pin<?> endPin;
    private transient final Pin<?> currentPin;
    private transient final Pin<?> completePin;

    private transient boolean needBreak = false;

    public ForLoopLogicAction(Context context) {
        super(context, R.string.action_for_loop_logic_title);
        startPin = addPin(new Pin<>(new PinInteger(1), context.getString(R.string.action_for_loop_logic_subtitle_start)));
        endPin = addPin(new Pin<>(new PinInteger(5), context.getString(R.string.action_for_loop_logic_subtitle_end)));
        currentPin = addPin(new Pin<>(new PinInteger(), context.getString(R.string.action_for_loop_logic_subtitle_curr), PinDirection.OUT, PinSlotType.MULTI));
        completePin = addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_for_loop_logic_subtitle_complete), PinDirection.OUT));
        addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_for_loop_logic_subtitle_break)));
    }

    public ForLoopLogicAction(JsonObject jsonObject) {
        super(jsonObject);
        startPin = addPin(tmpPins.remove(0));
        endPin = addPin(tmpPins.remove(0));
        currentPin = addPin(tmpPins.remove(0));
        completePin = addPin(tmpPins.remove(0));
        addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<?> pin) {
        if (pin.getId().equals(inPin.getId())) {
            needBreak = false;
            PinInteger start = (PinInteger) getPinValue(worldState, runnable.getTask(), startPin);
            PinInteger end = (PinInteger) getPinValue(worldState, runnable.getTask(), endPin);
            PinInteger current = (PinInteger) currentPin.getValue();
            for (int i = start.getValue(); i <= end.getValue(); i++) {
                if (runnable.isInterrupt()) return;
                if (needBreak) break;
                current.setValue(i);
                super.doAction(worldState, runnable, outPin);
            }
            super.doAction(worldState, runnable, completePin);
        } else {
            needBreak = true;
        }
    }
}
