package top.bogey.touch_tool_pro.bean.base;

import android.content.Context;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class LogInfo {
    private final long time;
    private final int index;
    private final String log;

    public LogInfo(int index, String log) {
        time = System.currentTimeMillis();
        this.log = log;
        this.index = index;
    }

    public long getTime() {
        return time;
    }

    public String getTimeString() {
        Context context = MainApplication.getInstance();
        return context.getString(R.string.date, AppUtils.formatDateLocalDate(context, time), AppUtils.formatDateLocalMillisecond(context, time));
    }

    public String getLog() {
        return log;
    }

    public String getLogString() {
        return "【" + index + "】 " + getTimeString() + "\n" + log;
    }

    public int getIndex() {
        return index;
    }
}
