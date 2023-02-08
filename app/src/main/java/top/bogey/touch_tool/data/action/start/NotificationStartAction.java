package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StartAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.ui.app.AppView;

public class NotificationStartAction extends StartAction {
    private transient final Pin appPin;
    private transient final Pin textPin;

    public NotificationStartAction(Context context) {
        super(context, R.string.action_notification_start_title);
        appPin = addPin(new Pin(new PinSelectApp(AppView.MULTI_MODE)));
        textPin = addPin(new Pin(new PinString(), context.getString(R.string.action_notification_start_subtitle_text)));
    }

    public NotificationStartAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = addPin(tmpPins.remove(0));
        textPin = addPin(tmpPins.remove(0));
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        String packageName = worldState.getNotificationPackage();
        if (packageName == null) return false;

        String notificationText = worldState.getNotificationText();
        if (notificationText == null) return false;

        PinString text = (PinString) textPin.getValue();
        if (text.getValue() == null || text.getValue().isEmpty()) return false;

        Pattern compile = Pattern.compile(text.getValue());
        boolean result = compile.matcher(notificationText).find();

        if (!result) return false;

        MainAccessibilityService service = MainApplication.getService();
        String commonPackageName = service.getString(R.string.common_package_name);

        PinSelectApp helper = (PinSelectApp) getPinValue(worldState, task, appPin);
        Map<String, ArrayList<String>> packages = helper.getPackages();

        // 包含通用且包含当前包，代表排除当前包
        if (packages.containsKey(commonPackageName) && packages.containsKey(packageName))
            return false;

        // 包含通用，直接返回准备好了
        if (packages.containsKey(commonPackageName)) return true;

        if (packages.containsKey(packageName)) {
            ArrayList<String> activityClasses = packages.get(packageName);
            if (activityClasses == null) return false;

            return activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName());
        }
        return false;
    }
}
