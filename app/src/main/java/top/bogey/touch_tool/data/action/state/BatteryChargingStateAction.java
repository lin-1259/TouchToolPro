package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.os.BatteryManager;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinSpinner;

public class BatteryChargingStateAction extends StateAction {
    private transient final Pin chargingStatePin;

    public BatteryChargingStateAction(Context context) {
        super(context, R.string.action_battery_charging_state_title);
        chargingStatePin = addPin(new Pin(new PinSpinner(context.getResources().getStringArray(R.array.charging_state)), context.getString(R.string.action_battery_charging_state_subtitle_state)));
    }

    public BatteryChargingStateAction(JsonObject jsonObject) {
        super(jsonObject);
        chargingStatePin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        WorldState worldState = WorldState.getInstance();
        int batteryState = worldState.getBatteryState();
        int chargingState = convertToChargingState(((PinSpinner) getPinValue(runnable, actionContext, chargingStatePin)).getIndex());
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
