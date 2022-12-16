package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinType;

public class BatteryStartAction extends StartAction {
    private final Pin<AtomicInteger> lowPin;
    private final Pin<AtomicInteger> highPin;
    private transient boolean inRnage = false;

    public BatteryStartAction() {
        super(ActionTag.START_BATTERY);
        lowPin = addPin(new Pin<>(PinType.INTEGER, R.string.battery_contidion_low, new AtomicInteger(1)));
        highPin = addPin(new Pin<>(PinType.INTEGER, R.string.battery_contidion_high, new AtomicInteger(100)));
        addPin(restartPin);
        titleId = R.string.task_type_battery;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        int batteryPercent = worldState.getBatteryPercent();
        int low = lowPin.getValue().get();
        int high = highPin.getValue().get();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        // 已经执行过了，电量未出范围不再重复执行
        if (result && inRnage) return false;
        inRnage = result;
        return inRnage;
    }
}
