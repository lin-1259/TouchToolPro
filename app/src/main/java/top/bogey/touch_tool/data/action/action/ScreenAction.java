package top.bogey.touch_tool.data.action.action;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.widget.Toast;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.utils.AppUtils;

public class ScreenAction extends NormalAction {
    private transient Pin screenPin = new Pin(new PinBoolean(true), R.string.action_screen_action_subtitle_state);

    public ScreenAction() {
        super(R.string.action_screen_action_title);
        screenPin = addPin(screenPin);
    }

    public ScreenAction(JsonObject jsonObject) {
        super(R.string.action_screen_action_title, jsonObject);
        screenPin = reAddPin(screenPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean state = (PinBoolean) getPinValue(runnable, actionContext, screenPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (state.getValue()) {
            AppUtils.wakeScreen(service);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
            } else {
                Toast.makeText(service, R.string.device_not_support_lock, Toast.LENGTH_SHORT).show();
            }
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
