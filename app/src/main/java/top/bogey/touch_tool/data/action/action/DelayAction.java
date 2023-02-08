package top.bogey.touch_tool.data.action.action;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class DelayAction extends NormalAction {
    private transient final Pin<?> delayPin;

    public DelayAction(Context context) {
        super(context, R.string.action_delay_action_title);
        delayPin = addPin(new Pin<>(new PinValueArea(100, 60000, 100, 300, 300)));
    }

    public DelayAction(JsonObject jsonObject) {
        super(jsonObject);
        delayPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<?> pin) {
        PinValueArea pinValueArea = (PinValueArea) getPinValue(worldState, runnable.getTask(), delayPin);
        sleep(pinValueArea.getRandomValue());
        super.doAction(worldState, runnable, outPin);
    }
}
