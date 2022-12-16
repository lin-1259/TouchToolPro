package top.bogey.touch_tool.data.action.start;

import android.content.Context;
import android.os.BatteryManager;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinArrayHelper;
import top.bogey.touch_tool.data.action.pin.PinType;

public class BatteryStateStartAction extends StartAction {
    private final Pin<PinArrayHelper> statePin;
    private transient int currState;

    public BatteryStateStartAction() {
        super(ActionTag.START_BATTERY_STATE);
        statePin = addPin(new Pin<>(PinType.ARRAY, R.string.battery_state_contidion_tips, new PinArrayHelper(R.array.charging_state)));
        addPin(restartPin);
        titleId = R.string.task_type_battery_state;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        int batteryState = worldState.getBatteryState();
        PinArrayHelper value = statePin.getValue();

        if (convertToChargingState(value.getIndex()) != batteryState) return false;

        // 当前已经是了，不再执行
        if (currState == batteryState) return false;
        currState = batteryState;
        return true;
    }

    public int convertToChargingState(int index) {
        switch (index) {
            case 0:
                return BatteryManager.BATTERY_STATUS_CHARGING;
            case 1:
                return BatteryManager.BATTERY_STATUS_DISCHARGING;
            case 2:
                return BatteryManager.BATTERY_STATUS_NOT_CHARGING;
            case 3:
                return BatteryManager.BATTERY_STATUS_FULL;
        }
        return BatteryManager.BATTERY_STATUS_UNKNOWN;
    }

    public int convertToIndex(int chargingState) {
        switch (chargingState) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return 0;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return 1;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return 2;
            case BatteryManager.BATTERY_STATUS_FULL:
                return 3;
        }
        return 0;
    }
}
