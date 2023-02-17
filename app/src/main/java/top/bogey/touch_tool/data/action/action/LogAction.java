package top.bogey.touch_tool.data.action.action;

import android.content.Context;

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
    private transient final Pin textPin;
    private transient final Pin toastPin;

    public LogAction(Context context) {
        super(context, R.string.action_log_action_title);
        textPin = addPin(new Pin(new PinString(), context.getString(R.string.action_log_action_subtitle_tips)));
        toastPin = addPin(new Pin(new PinBoolean(true), context.getString(R.string.action_log_action_subtitle_toast)));
    }

    public LogAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = addPin(tmpPins.remove(0));
        toastPin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinString pinString = (PinString) getPinValue(actionContext, textPin);

        MainAccessibilityService service = MainApplication.getService();
        TaskRepository.getInstance().addLog(runnable.getTask(), runnable.getStartAction().getTitle(), pinString.getValue());

        PinBoolean showToast = (PinBoolean) getPinValue(actionContext, toastPin);
        if (showToast.getValue()) {
            service.showToast(pinString.getValue());
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
