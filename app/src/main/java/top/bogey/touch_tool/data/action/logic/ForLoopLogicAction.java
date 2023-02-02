package top.bogey.touch_tool.data.action.logic;

import android.content.Context;
import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class ForLoopLogicAction extends NormalAction {
    private final Pin<? extends PinObject> startPin;
    private final Pin<? extends PinObject> endPin;
    private final Pin<? extends PinObject> currentPin;
    private final Pin<? extends PinObject> completePin;

    private boolean needBreak = false;

    public ForLoopLogicAction(Context context) {
        super(context, R.string.action_for_loop_logic_title);
        startPin = addPin(new Pin<>(new PinInteger(1), context.getString(R.string.action_for_loop_logic_subtitle_start)));
        endPin = addPin(new Pin<>(new PinInteger(5), context.getString(R.string.action_for_loop_logic_subtitle_end)));
        currentPin = addPin(new Pin<>(new PinInteger(), context.getString(R.string.action_for_loop_logic_subtitle_curr), PinDirection.OUT, PinSlotType.MULTI));
        completePin = addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_for_loop_logic_subtitle_complete), PinDirection.OUT));
        addPin(new Pin<>(new PinExecute(), context.getString(R.string.action_for_loop_logic_subtitle_break)));
    }

    public ForLoopLogicAction(Parcel in) {
        super(in);
        startPin = addPin(pinsTmp.remove(0));
        endPin = addPin(pinsTmp.remove(0));
        currentPin = addPin(pinsTmp.remove(0));
        completePin = addPin(pinsTmp.remove(0));
        addPin(pinsTmp.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
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
