package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinSelectAppHelper;
import top.bogey.touch_tool.data.action.pin.PinType;

public class NotificationStartAction extends StartAction {
    private final Pin<PinSelectAppHelper> appPin;
    private final Pin<AtomicReference<CharSequence>> textPin;

    public NotificationStartAction() {
        super(ActionTag.START_NOTIFICATION);
        appPin = addPin(new Pin<>(PinType.APP, new PinSelectAppHelper(PinSelectAppHelper.MULTI_MODE)));
        textPin = addPin(new Pin<>(PinType.STRING, R.string.notification_condition_tips, new AtomicReference<>()));
        addPin(restartPin);
        titleId = R.string.task_type_notification;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {

        CharSequence packageName = worldState.getPackageName();
        if (packageName == null) return false;

        PinSelectAppHelper helper = appPin.getValue();
        Map<CharSequence, List<CharSequence>> packages = helper.getPackages();
        List<CharSequence> activityClasses = packages.get(packageName);
        if (activityClasses == null) return false;

        AtomicReference<CharSequence> value = textPin.getValue();
        CharSequence text = value.get();
        if (text == null || text.length() == 0) return false;

        CharSequence notificationText = worldState.getNotificationText();
        if (notificationText == null) return false;

        return (activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName()) && notificationText.toString().contains(text));
    }
}
