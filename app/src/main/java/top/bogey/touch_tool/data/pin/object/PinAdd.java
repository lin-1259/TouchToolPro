package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinAdd extends PinObject {
    private final Pin pin;

    public PinAdd(Pin pin) {
        super();
        this.pin = pin;
    }

    public PinAdd(JsonObject jsonObject) {
        super(jsonObject);
        pin = new Pin(jsonObject.get("pin").getAsJsonObject());
    }

    public Pin getPin() {
        return pin;
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }
}
