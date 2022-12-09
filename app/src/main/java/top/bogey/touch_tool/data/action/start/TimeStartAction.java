package top.bogey.touch_tool.data.action.start;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;

public class TimeStartAction extends StartAction{
    // 开始时间
    private long startTime;
    // 间隔
    private long periodic;

    public TimeStartAction() {
        super(new String[]{ActionTag.START_TIME});
    }

    @Override
    public boolean isValid() {
        return periodic > 0 || (periodic == 0 && startTime > System.currentTimeMillis());
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getPeriodic() {
        return periodic;
    }

    public void setPeriodic(long periodic) {
        this.periodic = periodic;
    }
}
