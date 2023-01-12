package top.bogey.touch_tool.data.action.logic;

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

    public ForLoopLogicAction() {
        super();
        startPin = addPin(new Pin<>(new PinInteger(), R.string.action_for_loop_logic_subtitle_start));
        endPin = addPin(new Pin<>(new PinInteger(), R.string.action_for_loop_logic_subtitle_end));
        currentPin = addPin(new Pin<>(new PinInteger(), R.string.action_for_loop_logic_subtitle_curr, PinDirection.OUT, PinSlotType.MULTI));
        completePin = addPin(new Pin<>(new PinExecute(), R.string.action_for_loop_logic_subtitle_complete, PinDirection.OUT));
        titleId = R.string.action_for_loop_logic_title;
    }

    public ForLoopLogicAction(Parcel in) {
        super(in);
        startPin = addPin(pinsTmp.remove(0));
        endPin = addPin(pinsTmp.remove(0));
        currentPin = addPin(pinsTmp.remove(0));
        completePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_for_loop_logic_title;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinInteger start = (PinInteger) getPinValue(worldState, runnable.getTask(), startPin);
        PinInteger end = (PinInteger) getPinValue(worldState, runnable.getTask(), endPin);
        PinInteger current = (PinInteger) getPinValue(worldState, runnable.getTask(), currentPin);
        for (int i = start.getValue(); i <= end.getValue(); i++) {
            current.setValue(i);
            doAction(worldState, runnable, outPin);
        }
        doAction(worldState, runnable, completePin);
    }
}
