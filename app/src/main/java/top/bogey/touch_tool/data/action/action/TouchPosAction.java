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
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class TouchPosAction extends NormalAction {
    private transient final Pin<?> posPin;
    private transient final Pin<?> timePin;
    private transient final Pin<?> offsetPin;

    public TouchPosAction(Context context) {
        super(context, R.string.action_touch_pos_action_title);
        posPin = addPin(new Pin<>(new PinPoint(), context.getString(R.string.action_touch_pos_action_subtitle_position)));
        timePin = addPin(new Pin<>(new PinValueArea(100, 60000, 100, 100, 100), context.getString(R.string.action_touch_pos_action_subtitle_time)));
        offsetPin = addPin(new Pin<>(new PinBoolean(), context.getString(R.string.action_touch_pos_action_subtitle_offset)));
    }

    public TouchPosAction(JsonObject jsonObject) {
        super(jsonObject);
        posPin = addPin(tmpPins.remove(0));
        timePin = addPin(tmpPins.remove(0));
        offsetPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<?> pin) {
        PinPoint pos = (PinPoint) getPinValue(worldState, runnable.getTask(), posPin);
        PinValueArea timeArea = (PinValueArea) getPinValue(worldState, runnable.getTask(), timePin);
        PinBoolean offset = (PinBoolean) getPinValue(worldState, runnable.getTask(), offsetPin);

        MainAccessibilityService service = MainApplication.getService();
        int randomTime = timeArea.getRandomValue();
        service.runGesture(pos.getX(offset.getValue()), pos.getY(offset.getValue()), randomTime, null);
        sleep(randomTime);
        super.doAction(worldState, runnable, outPin);
    }
}
