package top.bogey.touch_tool.data;

import android.content.Context;

import java.util.UUID;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.AppUtils;

public class LogInfo {
    private final String id;
    private final long time;
    private final String taskId;
    private final String log;

    public LogInfo(String taskId, String log) {
        id = UUID.randomUUID().toString();
        time = System.currentTimeMillis();
        this.taskId = taskId;
        this.log = log;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getTime(Context context) {
        return context.getString(R.string.date, AppUtils.formatDateLocalDate(context, time), AppUtils.formatDateLocalMillisecond(context, time));
    }

    public String getTaskId() {
        return taskId;
    }

    public String getLog(Context context) {
        return getTime(context) + "\t" + log;
    }

    public String getLog() {
        return log;
    }
}
