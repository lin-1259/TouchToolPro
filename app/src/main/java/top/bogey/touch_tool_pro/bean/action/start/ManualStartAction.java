package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.bean.task.WorldState;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ManualStartAction extends StartAction {
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.MULTI_ALL_ACTIVITY), R.string.pin_app);

    public ManualStartAction() {
        super(ActionType.MANUAL_START);
        appPin = addPin(appPin);
    }

    public ManualStartAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getPackageName();
        if (packageName == null) return false;
        String activityName = worldState.getActivityName();

        MainAccessibilityService service = MainApplication.getInstance().getService();
        PinApplication appValue = (PinApplication) appPin.getValue();
        return appValue.contain(service, packageName, activityName);
    }
}
