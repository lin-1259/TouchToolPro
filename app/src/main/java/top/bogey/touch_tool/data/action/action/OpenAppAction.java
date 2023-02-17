package top.bogey.touch_tool.data.action.action;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
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
    private transient final Pin appPin;

    public OpenAppAction(Context context) {
        super(context, R.string.action_open_app_action_title);
        appPin = addPin(new Pin(new PinSelectApp(AppView.SINGLE_WITH_ACTIVITY_MODE)));
    }

    public OpenAppAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinSelectApp app = (PinSelectApp) getPinValue(actionContext, appPin);
        MainAccessibilityService service = MainApplication.getService();
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
