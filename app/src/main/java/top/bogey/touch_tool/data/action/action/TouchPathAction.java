package top.bogey.touch_tool.data.action.action;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class TouchPathAction extends NormalAction {
    private transient final Pin pathPin;
    private transient final Pin timePin;
    private transient final Pin offsetPin;

    public TouchPathAction(Context context) {
        super(context, R.string.action_touch_path_action_title);
        pathPin = addPin(new Pin(new PinPath(), context.getString(R.string.action_touch_path_action_subtitle_path)));
        timePin = addPin(new Pin(new PinValueArea(100, 60000, 100, 300, 300), context.getString(R.string.action_touch_path_action_subtitle_time)));
        offsetPin = addPin(new Pin(new PinBoolean(), context.getString(R.string.action_touch_path_action_subtitle_offset)));
    }

    public TouchPathAction(JsonObject jsonObject) {
        super(jsonObject);
        pathPin = addPin(tmpPins.remove(0));
        timePin = addPin(tmpPins.remove(0));
        offsetPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin pin) {
        PinPath pinPath = (PinPath) getPinValue(worldState, runnable.getTask(), pathPin);
        PinValueArea valueArea = (PinValueArea) getPinValue(worldState, runnable.getTask(), timePin);
        PinBoolean offset = (PinBoolean) getPinValue(worldState, runnable.getTask(), offsetPin);

        MainAccessibilityService service = MainApplication.getService();
        int randomTime = valueArea.getRandomValue();
        service.runGesture(pinPath.getRealPaths(service, offset.getValue()), randomTime, null);
        sleep(randomTime);
        super.doAction(worldState, runnable, outPin);
    }
}
