package top.bogey.touch_tool.data.action.start;

import java.util.regex.Pattern;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;

public class NotificationStartAction extends StartAction {
    private String text;

    public NotificationStartAction() {
        super(new String[]{ActionTag.START_NOTIFICATION});
    }

    @Override
    public boolean checkState(TaskHelper taskHelper) {
        Pattern pattern = Pattern.compile(text);
        String notificationText = taskHelper.getNotificationText();
        if (notificationText == null) return false;
        return pattern.matcher(notificationText).find();
    }

    @Override
    public boolean isValid() {
        return text != null && !text.isEmpty();
    }
}
