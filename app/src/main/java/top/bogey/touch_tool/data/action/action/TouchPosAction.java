package top.bogey.touch_tool.data.action.action;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class TouchPosAction extends NormalAction {
    private transient final Pin posPin;
    private transient final Pin timePin;
    private transient final Pin offsetPin;

    public TouchPosAction(Context context) {
        super(context, R.string.action_touch_pos_action_title);
        posPin = addPin(new Pin(new PinPoint(), context.getString(R.string.action_touch_pos_action_subtitle_position)));
        timePin = addPin(new Pin(new PinValueArea(100, 60000, 100, 100, 100), context.getString(R.string.action_touch_pos_action_subtitle_time)));
        offsetPin = addPin(new Pin(new PinBoolean(), context.getString(R.string.action_touch_pos_action_subtitle_offset)));
    }

    public TouchPosAction(JsonObject jsonObject) {
        super(jsonObject);
        posPin = addPin(tmpPins.remove(0));
        timePin = addPin(tmpPins.remove(0));
        offsetPin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinPoint pos = (PinPoint) getPinValue(runnable, actionContext, posPin);
        PinValueArea timeArea = (PinValueArea) getPinValue(runnable, actionContext, timePin);
        PinBoolean offset = (PinBoolean) getPinValue(runnable, actionContext, offsetPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        int randomTime = timeArea.getRandomValue();
        service.runGesture(pos.getX(offset.getValue()), pos.getY(offset.getValue()), randomTime, null);
        sleep(randomTime);
        doNextAction(runnable, actionContext, outPin);
    }
}
