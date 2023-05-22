package top.bogey.touch_tool.data.action.action;

import android.util.Log;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinString;

public class LogAction extends NormalAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.action_log_action_subtitle_tips);
    private transient Pin toastPin = new Pin(new PinBoolean(true), R.string.action_log_action_subtitle_toast);

    public LogAction() {
        super(R.string.action_log_action_title);
        textPin = addPin(textPin);
        toastPin = addPin(toastPin);
    }

    public LogAction(JsonObject jsonObject) {
        super(R.string.action_log_action_title, jsonObject);
        textPin = reAddPin(textPin);
        toastPin = reAddPin(toastPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinString pinString = (PinString) getPinValue(runnable, actionContext, textPin);

        Log.d("TAG", "LogAction: " + pinString.getValue());

        MainAccessibilityService service = MainApplication.getInstance().getService();
        TaskRepository.getInstance().addLog(runnable.getStartTask(), runnable.getStartAction().getTitle(service), pinString.getValue());

        PinBoolean showToast = (PinBoolean) getPinValue(runnable, actionContext, toastPin);
        if (showToast.getValue()) {
            service.showToast(pinString.getValue());
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
