package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinAdd<P extends PinObject> extends PinObject {
    private final Pin<P> pin;

    public PinAdd(Pin<P> pin) {
        super();
        this.pin = pin;
    }

    public PinAdd(JsonObject jsonObject) {
        super(jsonObject);
        Pin.PinDeserializer<P> pinDeserializer = new Pin.PinDeserializer<>();
        pin = pinDeserializer.deserialize(jsonObject.get("pin"), null, null);
    }

    public Pin<P> getPin() {
        return pin;
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }
}
