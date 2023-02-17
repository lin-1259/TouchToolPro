package top.bogey.touch_tool.data.action.state;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class BatteryStateAction extends StateAction {
    private transient final Pin areaPin;

    public BatteryStateAction(Context context) {
        super(context, R.string.action_battery_state_title);
        areaPin = addPin(new Pin(new PinValueArea(1, 100, 1), context.getString(R.string.action_battery_state_subtitle_battery)));
    }

    public BatteryStateAction(JsonObject jsonObject) {
        super(jsonObject);
        areaPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        WorldState worldState = WorldState.getInstance();
        int batteryPercent = worldState.getBatteryPercent();
        PinValueArea valueArea = (PinValueArea) getPinValue(actionContext, areaPin);
        int low = valueArea.getCurrMin();
        int high = valueArea.getCurrMax();
        boolean result = batteryPercent >= low && batteryPercent <= high;
        value.setValue(result);
    }
}
