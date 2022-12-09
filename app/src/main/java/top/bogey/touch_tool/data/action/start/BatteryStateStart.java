package top.bogey.touch_tool.data.action.start;

import android.os.BatteryManager;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;

public class BatteryStateStart extends StartAction{
    private int state;

    public BatteryStateStart() {
        super(new String[]{ActionTag.START_BATTERY_STATE});
    }

    @Override
    public boolean checkState(TaskHelper taskHelper) {
        int batteryState = taskHelper.getBatteryState();
        return batteryState == state;
    }

    @Override
    public boolean isValid() {
        return state > 0 && state != BatteryManager.BATTERY_STATUS_UNKNOWN;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
