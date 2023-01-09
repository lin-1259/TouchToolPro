package top.bogey.touch_tool.data.action.action;

import android.os.Parcel;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinTimeArea;

public class TouchPosAction extends NormalAction {
    private final Pin<? extends PinObject> posPin;
    private final Pin<? extends PinObject> timePin;
    private final Pin<? extends PinObject> offsetPin;

    public TouchPosAction() {
        super();
        posPin = addPin(new Pin<>(new PinPoint(), R.string.action_touch_pos_action_subtitle_position));
        timePin = addPin(new Pin<>(new PinTimeArea(300, TimeUnit.MILLISECONDS), R.string.action_touch_pos_action_subtitle_time));
        offsetPin = addPin(new Pin<>(new PinBoolean(), R.string.action_touch_pos_action_subtitle_offset));
        titleId = R.string.action_touch_pos_action_title;
    }

    public TouchPosAction(Parcel in) {
        super(in);
        posPin = addPin(pinsTmp.remove(0));
        timePin = addPin(pinsTmp.remove(0));
        offsetPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_touch_pos_action_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        PinPoint pos = (PinPoint) getPinValue(worldState, runnable.getTask(), posPin);
        PinTimeArea timeArea = (PinTimeArea) getPinValue(worldState, runnable.getTask(), timePin);
        PinBoolean offset = (PinBoolean) getPinValue(worldState, runnable.getTask(), offsetPin);

        MainAccessibilityService service = MainApplication.getService();
        int randomTime = timeArea.getRandomTime();
        service.runGesture(pos.getX(offset.getValue()), pos.getY(offset.getValue()), randomTime, null);
        boolean sleep = sleep(randomTime);
        if (!sleep) return;
        super.doAction(worldState, runnable);
    }
}
