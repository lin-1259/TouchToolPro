package top.bogey.touch_tool.data.action.action;

import android.os.Looper;
import android.os.Parcel;
import android.widget.Toast;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;

public class ToastAction extends NormalAction {
    private final Pin<? extends PinObject> textPin;

    public ToastAction() {
        super();
        textPin = addPin(new Pin<>(new PinString(), R.string.action_toast_action_subtitle_tips));
        titleId = R.string.action_toast_action_title;
    }

    public ToastAction(Parcel in) {
        super(in);
        textPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_toast_action_title;
    }

    @Override
    public boolean doAction(WorldState worldState, TaskRunnable runnable) {
        PinString pinString = (PinString) getPinValue(worldState, runnable.getTask(), textPin);
        Looper.prepare();
        Toast.makeText(MainApplication.getService(), pinString.getValue(), Toast.LENGTH_SHORT).show();
        Looper.loop();
        Looper.myLooper().quit();
        return super.doAction(worldState, runnable);
    }
}
