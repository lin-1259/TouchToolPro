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
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class SystemAbilityAction extends NormalAction {
    private final Pin<? extends PinObject> abilityPin;

    public SystemAbilityAction() {
        super();
        abilityPin = addPin(new Pin<>(new PinSpinner(R.array.system_ability)));
        titleId = R.string.action_system_ability_action_title;
    }

    public SystemAbilityAction(Parcel in) {
        super(in);
        abilityPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_system_ability_action_title;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin<? extends PinObject> pin) {
        PinSpinner ability = (PinSpinner) getPinValue(worldState, runnable.getTask(), abilityPin);
        MainAccessibilityService service = MainApplication.getService();
        switch (ability.getIndex()) {
            case 0:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case 1:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case 2:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case 3:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                break;
            case 4:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT);
                } else {
                    Toast.makeText(service, R.string.action_device_not_support_snap, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.doAction(worldState, runnable);
    }
}
