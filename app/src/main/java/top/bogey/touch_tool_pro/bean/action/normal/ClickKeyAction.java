package top.bogey.touch_tool_pro.bean.action.normal;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ClickKeyAction extends NormalAction {
    private transient Pin keyPin = new Pin(new PinSpinner(R.array.system_ability));

    public ClickKeyAction() {
        super(ActionType.CLICK_KEY);
        keyPin = addPin(keyPin);
    }

    public ClickKeyAction(JsonObject jsonObject) {
        super(jsonObject);
        keyPin = reAddPin(keyPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinSpinner key = (PinSpinner) getPinValue(runnable, context, keyPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        switch (key.getIndex()) {
            case 0 -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            case 1 -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            case 2 -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
            case 3 -> service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
            case 4 -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
                } else {
                    service.showToast(service.getString(R.string.device_not_support_snap));
                }
            }
        }
        executeNext(runnable, context, outPin);
    }

    public Pin getKeyPin() {
        return keyPin;
    }
}
