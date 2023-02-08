package top.bogey.touch_tool.data.action.action;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.utils.AppUtils;

public class ScreenAction extends NormalAction {
    private transient final Pin screenPin;

    public ScreenAction(Context context) {
        super(context, R.string.action_screen_action_title);
        screenPin = addPin(new Pin(new PinBoolean(true), context.getString(R.string.action_screen_action_subtitle_state)));
    }

    public ScreenAction(JsonObject jsonObject) {
        super(jsonObject);
        screenPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin pin) {
        PinBoolean state = (PinBoolean) getPinValue(worldState, runnable.getTask(), screenPin);
        MainAccessibilityService service = MainApplication.getService();
        if (state.getValue()) {
            AppUtils.wakeScreen(service);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
            } else {
                Toast.makeText(service, R.string.device_not_support_lock, Toast.LENGTH_SHORT).show();
            }
        }
        super.doAction(worldState, runnable, outPin);
    }
}
