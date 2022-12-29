package top.bogey.touch_tool.data.action.state;

import android.os.BatteryManager;
import android.os.Parcel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinBoolean;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.data.action.pin.object.PinSpinner;

public class BatteryChargingStateAction extends StateAction {
    private final Pin<? extends PinObject> chargingStatePin;

    public BatteryChargingStateAction() {
        super();
        chargingStatePin = addPin(new Pin<>(new PinSpinner(R.array.charging_state), R.string.action_battery_charging_state_subtitle_state));
        titleId = R.string.action_battery_charging_state_title;
    }

    public BatteryChargingStateAction(Parcel in) {
        super(in);
        chargingStatePin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_battery_charging_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        int batteryState = worldState.getBatteryState();
        int chargingState = convertToChargingState(((PinSpinner) chargingStatePin.getValue()).getIndex());
        value.setValue(chargingState == batteryState);
    }

    public static int convertToChargingState(int index) {
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

    public static int convertToIndex(int chargingState) {
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
