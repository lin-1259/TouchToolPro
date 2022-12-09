package top.bogey.touch_tool.data.action.start;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;

public class BatteryStart extends StartAction{
    private int low;
    private int high;

    public BatteryStart() {
        super(new String[]{ActionTag.START_BATTERY});
    }

    @Override
    public boolean checkState(TaskHelper taskHelper) {
        int baterryPercent = taskHelper.getBaterryPercent();
        return baterryPercent >= low && baterryPercent <= high;
    }

    @Override
    public boolean isValid() {
        return low + high > 0;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }
}
