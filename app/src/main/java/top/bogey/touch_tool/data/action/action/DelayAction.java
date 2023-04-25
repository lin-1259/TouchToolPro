package top.bogey.touch_tool.data.action.action;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class DelayAction extends NormalAction {
    private transient Pin delayPin = new Pin(new PinValueArea(50, 60000, 50, 300, 300));

    public DelayAction() {
        super(R.string.action_delay_action_title);
        delayPin = addPin(delayPin);
    }

    public DelayAction(JsonObject jsonObject) {
        super(R.string.action_delay_action_title, jsonObject);
        delayPin = reAddPin(delayPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinValueArea pinValueArea = (PinValueArea) getPinValue(runnable, actionContext, delayPin);
        sleep(pinValueArea.getRandomValue());
        doNextAction(runnable, actionContext, outPin);
    }
}
