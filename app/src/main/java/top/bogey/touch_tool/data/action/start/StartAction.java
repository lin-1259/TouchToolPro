package top.bogey.touch_tool.data.action.start;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class StartAction extends BaseAction {
    protected transient Pin outPin = new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT);
    protected transient Pin enablePin = new Pin(new PinBoolean(true), R.string.action_start_subtitle_enable);
    protected transient Pin restartPin = new Pin(new PinSpinner(R.array.restart_type), R.string.action_start_subtitle_restart);

    public StartAction(@StringRes int titleId) {
        super(titleId);
        outPin = addPin(outPin);
        enablePin = addPin(enablePin);
        restartPin = addPin(restartPin);
    }

    public StartAction(@StringRes int titleId, JsonObject jsonObject) {
        super(titleId, jsonObject);
        outPin = reAddPin(outPin);
        enablePin = reAddPin(enablePin);
        restartPin = reAddPin(restartPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        doNextAction(runnable, actionContext, outPin);
    }

    // 开始动作只做开始检测
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        return true;
    }

    public boolean checkStop(TaskRunnable runnable, ActionContext actionContext) {return false;}

    public boolean isEnable() {
        return ((PinBoolean) enablePin.getValue()).getValue();
    }

    public void setEnable(boolean enable) {
        ((PinBoolean) enablePin.getValue()).setValue(enable);
    }

    public RestartType getRestartType() {
        PinSpinner value = (PinSpinner) restartPin.getValue();
        return RestartType.values()[value.getIndex()];
    }

    public Pin getOutPin() {
        return outPin;
    }
}
