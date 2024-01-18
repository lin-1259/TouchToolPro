package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.service.WorldState;

public class ManualStartAction extends StartAction {
    private final transient LinkedHashMap<String, ArrayList<String>> apps = new LinkedHashMap<>(Collections.singletonMap(MainApplication.getInstance().getString(R.string.common_package_name), new ArrayList<>()));
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.MULTI_ALL_ACTIVITY, apps), R.string.pin_app);

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
