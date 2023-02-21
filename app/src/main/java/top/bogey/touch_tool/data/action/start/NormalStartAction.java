package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class NormalStartAction extends StartAction {
    private transient final Pin startPin;

    public NormalStartAction(Context context) {
        super(context, R.string.action_normal_start_title);
        startPin = addPin(new Pin(new PinBoolean(false), context.getString(R.string.action_normal_start_subtitle_condition)));
    }

    public NormalStartAction(JsonObject jsonObject) {
        super(jsonObject);
        startPin = addPin(tmpPins.remove(0));
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        PinBoolean value = (PinBoolean) getPinValue(runnable, actionContext, startPin);
        return value.getValue();
    }
}
