package top.bogey.touch_tool.data.action.action;

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
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_touch_pos_action_subtitle_position);
    private transient Pin timePin = new Pin(new PinValueArea(50, 60000, 50, 100, 100), R.string.action_touch_pos_action_subtitle_time);
    private transient Pin offsetPin = new Pin(new PinBoolean(), R.string.action_touch_pos_action_subtitle_offset);

    public TouchPosAction() {
        super(R.string.action_touch_pos_action_title);
        posPin = addPin(posPin);
        timePin = addPin(timePin);
        offsetPin = addPin(offsetPin);
    }

    public TouchPosAction(JsonObject jsonObject) {
        super(R.string.action_touch_pos_action_title, jsonObject);
        posPin = reAddPin(posPin);
        timePin = reAddPin(timePin);
        offsetPin = reAddPin(offsetPin);
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

    public Pin getPosPin() {
        return posPin;
    }
}
