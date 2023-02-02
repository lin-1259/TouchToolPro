package top.bogey.touch_tool.data.action;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.StringRes;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class CalculateAction extends BaseAction {
    public CalculateAction(Context context, @StringRes int titleId) {
        super(context, titleId);
    }

    public CalculateAction(Parcel in) {
        super(in);
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
    }
}
