package top.bogey.touch_tool.data.action;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;

public class CalculateAction extends BaseAction {
    public CalculateAction(Context context, @StringRes int titleId) {
        super(context, titleId);
    }

    public CalculateAction(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<?> pin) {
    }
}
