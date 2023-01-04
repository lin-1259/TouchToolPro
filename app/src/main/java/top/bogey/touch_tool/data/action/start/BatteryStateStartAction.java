package top.bogey.touch_tool.data.action.start;

import android.os.BatteryManager;
import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;
import top.bogey.touch_tool.data.action.pin.PinSubType;

public class BatteryStateStartAction extends StartAction {
    private final Pin<? extends PinObject> statePin;
    private transient int currState;

    public BatteryStateStartAction() {
        super();
        statePin = addPin(new Pin<>(new PinSpinner(R.array.charging_state), R.string.action_battery_charging_state_subtitle_state));
        titleId = R.string.action_battery_charging_state_title;
    }

    public BatteryStateStartAction(Parcel in) {
        super(in);
        statePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_battery_charging_state_title;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        int batteryState = worldState.getBatteryState();

        PinSpinner value = (PinSpinner) statePin.getValue();
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
