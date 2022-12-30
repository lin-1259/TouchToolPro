package top.bogey.touch_tool.data.action.start;

import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinSubType;
import top.bogey.touch_tool.data.action.pin.object.PinInteger;
import top.bogey.touch_tool.data.action.pin.object.PinObject;

public class BatteryStartAction extends StartAction {
    private final Pin<? extends PinObject> lowPin;
    private final Pin<? extends PinObject> highPin;
    private transient boolean inRange = false;

    public BatteryStartAction() {
        super();
        lowPin = addPin(new Pin<>(new PinInteger(1), R.string.battery_contidion_low));
        highPin = addPin(new Pin<>(new PinInteger(100), R.string.battery_contidion_high));
        titleId = R.string.task_type_battery;
    }

    public BatteryStartAction(Parcel in) {
        super(in);
        lowPin = addPin(pinsTmp.remove(0));
        highPin = addPin(pinsTmp.remove(0));
        titleId = R.string.task_type_battery;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        int batteryPercent = worldState.getBatteryPercent();
        int low = ((PinInteger) lowPin.getValue()).getValue();
        int high = ((PinInteger) highPin.getValue()).getValue();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        // 已经执行过了，电量未出范围不再重复执行
        if (result && inRange) return false;
        inRange = result;
        return inRange;
    }
}
