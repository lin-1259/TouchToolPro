package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinAdd extends PinObject {
    private final Pin pin;
    private final int offset;

    public PinAdd() {
        this(new Pin(new PinObject()));
    }

    public PinAdd(Pin pin) {
        super();
        this.pin = pin;
        offset = 1;
    }

    public PinAdd(Pin pin, int offset) {
        this.pin = pin;
        this.offset = offset;
    }

    public PinAdd(JsonObject jsonObject) {
        super(jsonObject);
        pin = new Pin(jsonObject.get("pin").getAsJsonObject());
        JsonElement element = jsonObject.get("offset");
        if (element != null) offset = element.getAsInt();
        else offset = 1;
    }

    public Pin getPin() {
        return pin;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }
}
