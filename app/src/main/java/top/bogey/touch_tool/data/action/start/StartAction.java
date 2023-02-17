package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class StartAction extends BaseAction {
    protected transient final Pin outPin;
    protected transient final Pin enablePin;
    protected transient final Pin restartPin;

    public StartAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        outPin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_subtitle_execute), PinDirection.OUT, PinSlotType.SINGLE));
        enablePin = addPin(new Pin(new PinBoolean(true), context.getString(R.string.action_start_subtitle_enable)));
        restartPin = addPin(new Pin(new PinSpinner(context.getResources().getStringArray(R.array.restart_type)), context.getString(R.string.action_start_subtitle_restart)));
    }

    public StartAction(JsonObject jsonObject) {
        super(jsonObject);
        outPin = addPin(tmpPins.remove(0));
        enablePin = addPin(tmpPins.remove(0));
        restartPin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        doNextAction(runnable, actionContext, outPin);
    }

    // 开始动作只做开始检测
    public boolean checkReady(ActionContext actionContext) {
        return true;
    }

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
}
