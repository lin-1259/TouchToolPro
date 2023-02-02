package top.bogey.touch_tool.data.action.action;

import android.content.Context;
import android.os.Parcel;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinTimeArea;

public class TouchPathAction extends NormalAction {
    private final Pin<? extends PinObject> pathPin;
    private final Pin<? extends PinObject> timePin;
    private final Pin<? extends PinObject> offsetPin;

    public TouchPathAction(Context context) {
        super(context, R.string.action_touch_path_action_title);
        pathPin = addPin(new Pin<>(new PinPath(), context.getString(R.string.action_touch_path_action_subtitle_path)));
        timePin = addPin(new Pin<>(new PinTimeArea(300, TimeUnit.MILLISECONDS), context.getString(R.string.action_touch_path_action_subtitle_time)));
        offsetPin = addPin(new Pin<>(new PinBoolean(), context.getString(R.string.action_touch_path_action_subtitle_offset)));
    }

    public TouchPathAction(Parcel in) {
        super(in);
        pathPin = addPin(pinsTmp.remove(0));
        timePin = addPin(pinsTmp.remove(0));
        offsetPin = addPin(pinsTmp.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinPath pinPath = (PinPath) getPinValue(worldState, runnable.getTask(), pathPin);
        PinTimeArea timeArea = (PinTimeArea) getPinValue(worldState, runnable.getTask(), timePin);
        PinBoolean offset = (PinBoolean) getPinValue(worldState, runnable.getTask(), offsetPin);

        MainAccessibilityService service = MainApplication.getService();
        int randomTime = timeArea.getRandomTime();
        service.runGesture(pinPath.getRealPaths(service, offset.getValue()), randomTime, null);
        sleep(randomTime);
        super.doAction(worldState, runnable, outPin);
    }
}
