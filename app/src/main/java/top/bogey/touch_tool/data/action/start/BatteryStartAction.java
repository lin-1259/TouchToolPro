package top.bogey.touch_tool.data.action.start;

import android.content.Context;
import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class BatteryStartAction extends StartAction {
    private final Pin<? extends PinObject> areaPin;
    private transient boolean inRange = false;

    public BatteryStartAction(Context context) {
        super(context, R.string.action_battery_start_title);
        areaPin = addPin(new Pin<>(new PinValueArea(1, 100, 1), context.getString(R.string.action_battery_start_subtitle_battery)));
    }

    public BatteryStartAction(Parcel in) {
        super(in);
        areaPin = addPin(pinsTmp.remove(0));
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        int batteryPercent = worldState.getBatteryPercent();
        PinValueArea valueArea = (PinValueArea) getPinValue(worldState, task, areaPin);
        int low = valueArea.getCurrMin();
        int high = valueArea.getCurrMax();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        // 已经执行过了，电量未出范围不再重复执行
        if (result && inRange) return false;
        inRange = result;
        return inRange;
    }
}
