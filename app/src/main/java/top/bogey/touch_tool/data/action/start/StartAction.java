package top.bogey.touch_tool.data.action.start;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.StringRes;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class StartAction extends BaseAction {
    protected final Pin<? extends PinObject> enablePin;
    protected final Pin<? extends PinObject> restartPin;

    public StartAction(Context context, @StringRes int titleId) {
        super(context, titleId);
        addPin(outPin);
        enablePin = addPin(new Pin<>(new PinBoolean(true), context.getString(R.string.action_start_subtitle_enable)));
        restartPin = addPin(new Pin<>(new PinSpinner(R.array.restart_type), context.getString(R.string.action_start_subtitle_restart)));
    }

    public StartAction(Parcel in) {
        super(in);
        outPin = addPin(pinsTmp.remove(0));
        enablePin = addPin(pinsTmp.remove(0));
        restartPin = addPin(pinsTmp.remove(0));
    }

    // 开始动作只做开始检测
    public boolean checkReady(WorldState worldState, Task task) {
        return true;
    }

    public boolean isEnable() {
        return ((PinBoolean) enablePin.getValue()).getValue();
    }

    public RestartType getRestartType() {
        return RestartType.START_NEW;
    }
}
