package top.bogey.touch_tool.data.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class NormalStartAction extends StartAction {
    private transient Pin startPin = new Pin(new PinBoolean(false), R.string.action_normal_start_subtitle_condition);

    public NormalStartAction() {
        super(R.string.action_normal_start_title);
        startPin = addPin(startPin);
    }

    public NormalStartAction(JsonObject jsonObject) {
        super(R.string.action_normal_start_title, jsonObject);
        startPin = reAddPin(startPin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        PinBoolean value = (PinBoolean) getPinValue(runnable, actionContext, startPin);
        return value.getValue();
    }
}
