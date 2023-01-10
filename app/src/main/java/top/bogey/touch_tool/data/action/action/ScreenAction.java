package top.bogey.touch_tool.data.action.action;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Parcel;
import android.widget.Toast;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.AppUtils;

public class ScreenAction extends NormalAction {
    private final Pin<? extends PinObject> screenPin;

    public ScreenAction() {
        super();
        screenPin = addPin(new Pin<>(new PinBoolean(true), R.string.action_screen_action_subtitle_state));
        titleId = R.string.action_screen_action_title;
    }

    public ScreenAction(Parcel in) {
        super(in);
        screenPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_screen_action_title;
    }

    @Override
    public void doAction(WorldState worldState, TaskRunnable runnable) {
        PinBoolean state = (PinBoolean) getPinValue(worldState, runnable.getTask(), screenPin);
        MainAccessibilityService service = MainApplication.getService();
        if (state.getValue()) {
            AppUtils.wakeScreen(service);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN);
            } else {
                Toast.makeText(service, R.string.action_device_not_support_lock, Toast.LENGTH_SHORT).show();
            }
        }
        super.doAction(worldState, runnable);
    }
}
