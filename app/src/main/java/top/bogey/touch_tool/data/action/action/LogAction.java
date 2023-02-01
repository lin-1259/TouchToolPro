package top.bogey.touch_tool.data.action.action;

import android.os.Handler;
import android.os.Parcel;
import android.widget.Toast;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;

public class LogAction extends NormalAction {
    private final Pin<? extends PinObject> textPin;
    private final Pin<? extends PinObject> toastPin;

    public LogAction() {
        super();
        textPin = addPin(new Pin<>(new PinString(), R.string.action_log_action_subtitle_tips));
        toastPin = addPin(new Pin<>(new PinBoolean(false), R.string.action_log_action_subtitle_toast));
        titleId = R.string.action_log_action_title;
    }

    public LogAction(Parcel in) {
        super(in);
        textPin = addPin(pinsTmp.remove(0));
        toastPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_log_action_title;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinString pinString = (PinString) getPinValue(worldState, runnable.getTask(), textPin);

        MainAccessibilityService service = MainApplication.getService();
        TaskRepository.getInstance().addLog(runnable.getTask(), runnable.getStartAction().getTitle(service).toString(), pinString.getValue());

        PinBoolean showToast = (PinBoolean) getPinValue(worldState, runnable.getTask(), toastPin);
        if (showToast.getValue()) {
            service.showToast(pinString.getValue());
        }
        super.doAction(worldState, runnable, outPin);
    }
}
