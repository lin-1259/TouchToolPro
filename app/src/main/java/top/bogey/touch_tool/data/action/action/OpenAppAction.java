package top.bogey.touch_tool.data.action.action;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.ui.app.AppView;
import top.bogey.touch_tool.utils.AppUtils;

public class OpenAppAction extends NormalAction {
    private transient Pin appPin = new Pin(new PinSelectApp(AppView.SINGLE_WITH_ACTIVITY_MODE));

    public OpenAppAction() {
        super(R.string.action_open_app_action_title);
        appPin = addPin(appPin);
    }

    public OpenAppAction(JsonObject jsonObject) {
        super(R.string.action_open_app_action_title, jsonObject);
        appPin = reAddPin(appPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinSelectApp app = (PinSelectApp) getPinValue(runnable, actionContext, appPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        LinkedHashMap<String, ArrayList<String>> packages = app.getPackages();
        for (Map.Entry<String, ArrayList<String>> entry : packages.entrySet()) {
            ArrayList<String> value = entry.getValue();
            if (value == null) continue;
            if (value.size() > 0)
                AppUtils.gotoActivity(service, entry.getKey(), value.get(0));
            else AppUtils.gotoApp(service, entry.getKey());
            break;
        }
        doNextAction(runnable, actionContext, outPin);
    }
}
