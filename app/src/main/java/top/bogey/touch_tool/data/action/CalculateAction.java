package top.bogey.touch_tool.data.action;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.pin.Pin;

public class CalculateAction extends BaseAction {
    public CalculateAction(@StringRes int titleId) {
        super(titleId);
    }

    public CalculateAction(@StringRes int titleId, JsonObject jsonObject) {
        super(titleId, jsonObject);
    }

    @Override
    protected void doNextAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
    }
}
