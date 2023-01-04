package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.action.pin.PinSubType;
import top.bogey.touch_tool.data.action.pin.object.PinString;
import top.bogey.touch_tool.ui.app.AppView;

public class NotificationStartAction extends StartAction {
    private final Pin<? extends PinObject> appPin;
    private final Pin<? extends PinObject> textPin;

    public NotificationStartAction() {
        super();
        appPin = addPin(new Pin<>(new PinSelectApp(AppView.MULTI_MODE)));
        textPin = addPin(new Pin<>(new PinString(), R.string.action_notification_start_subtitle_text));
        titleId = R.string.action_notification_start_title;
    }

    public NotificationStartAction(Parcel in) {
        super(in);
        appPin = addPin(pinsTmp.remove(0));
        textPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_notification_start_title;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {

        CharSequence packageName = worldState.getPackageName();
        if (packageName == null) return false;

        PinSelectApp helper = (PinSelectApp) appPin.getValue();
        Map<CharSequence, ArrayList<CharSequence>> packages = helper.getPackages();
        ArrayList<CharSequence> activityClasses = packages.get(packageName);
        if (activityClasses == null) return false;

        PinString value = (PinString) textPin.getValue();
        CharSequence text = value.getValue();
        if (text == null || text.length() == 0) return false;

        CharSequence notificationText = worldState.getNotificationText();
        if (notificationText == null) return false;

        return (activityClasses.isEmpty() || activityClasses.contains(worldState.getActivityName()) && notificationText.toString().contains(text));
    }
}
