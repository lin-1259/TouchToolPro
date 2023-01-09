package top.bogey.touch_tool.data.action.logic;

import android.os.Parcel;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class SequenceLogicAction extends BaseAction {
    private final Pin<? extends PinObject> firstPin;

    public SequenceLogicAction() {
        super();
        addPin(inPin);
        firstPin = addPin(new Pin<>(new PinExecute(), R.string.action_sequence_logic_subtitle_start, PinDirection.OUT));
        addPin(new Pin<>(new PinExecute(), R.string.action_sequence_logic_subtitle_start, PinDirection.OUT));
        Pin<PinExecute> executePin = new Pin<>(new PinExecute(), R.string.action_sequence_logic_subtitle_start, PinDirection.OUT);
        addPin(new Pin<>(new PinAdd(executePin), R.string.action_subtitle_add_pin, PinDirection.OUT, PinSlotType.EMPTY));
        titleId = R.string.action_sequence_logic_title;
    }

    public SequenceLogicAction(Parcel in) {
        super(in);
        inPin = addPin(pinsTmp.remove(0));
        firstPin = addPin(pinsTmp.remove(0));
        for (Pin<? extends PinObject> pin : pinsTmp) {
            addPin(pin);
        }
        pinsTmp.clear();
        titleId = R.string.action_sequence_logic_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        ArrayList<Pin<? extends PinObject>> pins = getPins();
        int i = pins.indexOf(firstPin);
        for (; i < pins.size() - 1; i++) {
            doAction(worldState, runnable, pins.get(i));
        }
    }
}
