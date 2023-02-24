package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.ui.app.AppView;

public class ManualStartAction extends StartAction {
    private transient final Pin appPin;

    public ManualStartAction(Context context) {
        super(context, R.string.action_manual_start_title);
        appPin = addPin(new Pin(new PinSelectApp(AppView.MULTI_MODE)));
    }

    public ManualStartAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = addPin(tmpPins.remove(0));
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        WorldState worldState = WorldState.getInstance();
        String packageName = worldState.getPackageName();
        if (packageName == null) return false;

        MainAccessibilityService service = MainApplication.getInstance().getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) appPin.getValue();
        Map<String, ArrayList<String>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName)) {
            return false;
        }

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) {
            return true;
        }

        return packages.containsKey(packageName);
    }
}
