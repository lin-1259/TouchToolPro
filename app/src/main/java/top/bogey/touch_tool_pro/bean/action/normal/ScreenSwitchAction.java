package top.bogey.touch_tool_pro.bean.action.normal;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.widget.Toast;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class ScreenSwitchAction extends NormalAction{
    private transient Pin screenPin = new Pin(new PinBoolean(true), R.string.action_screen_action_subtitle_state);

    public ScreenSwitchAction() {
        super(ActionType.SCREEN_SWITCH);
        screenPin = addPin(screenPin);
    }

    public ScreenSwitchAction(JsonObject jsonObject) {
        super(jsonObject);
        screenPin = reAddPin(screenPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean screen = (PinBoolean) getPinValue(runnable, context, screenPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (screen.isBool()) {
            AppUtils.wakeScreen(service);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
            } else {
                Toast.makeText(service, R.string.device_not_support_lock, Toast.LENGTH_SHORT).show();
            }
        }
        runnable.sleep(100);
        executeNext(runnable, context, outPin);
    }
}
