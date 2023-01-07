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
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinTimeArea;

public class TouchPathAction extends NormalAction {
    private final Pin<? extends PinObject> pathPin;
    private final Pin<? extends PinObject> timePin;
    private final Pin<? extends PinObject> offsetPin;

    public TouchPathAction() {
        super();
        pathPin = addPin(new Pin<>(new PinPath(), R.string.action_touch_path_action_subtitle_path));
        timePin = addPin(new Pin<>(new PinTimeArea(300, TimeUnit.MILLISECONDS), R.string.action_touch_path_action_subtitle_time));
        offsetPin = addPin(new Pin<>(new PinBoolean(), R.string.action_touch_path_action_subtitle_offset));
        titleId = R.string.action_touch_path_action_title;
    }

    public TouchPathAction(Parcel in) {
        super(in);
        pathPin = addPin(pinsTmp.remove(0));
        timePin = addPin(pinsTmp.remove(0));
        offsetPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_touch_path_action_title;
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        PinPath pinPath = (PinPath) getPinValue(worldState, runnable.getTask(), pathPin);
        PinTimeArea timeArea = (PinTimeArea) getPinValue(worldState, runnable.getTask(), timePin);
        PinBoolean offset = (PinBoolean) getPinValue(worldState, runnable.getTask(), offsetPin);

        MainAccessibilityService service = MainApplication.getService();
        int randomTime = timeArea.getRandomTime();
        service.runGesture(pinPath.getRealPaths(service, offset.getValue()), randomTime, null);
        boolean sleep = sleep(randomTime);
        if (sleep) return super.doAction(worldState, runnable);
        return false;
    }
}
