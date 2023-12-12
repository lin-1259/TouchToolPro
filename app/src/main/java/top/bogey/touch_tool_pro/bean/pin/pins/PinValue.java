package top.bogey.touch_tool_pro.bean.pin.pins;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.PinType;

public class PinValue extends PinObject {
    public PinValue() {
        this(PinType.VALUE);
    }

    public PinValue(PinType type) {
        super(type);
    }

    public PinValue(PinType type, PinSubType subType) {
        super(type, subType);
    }

    public PinValue(JsonObject jsonObject) {
        super(jsonObject);
    }

    public boolean cast(String value) {
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }
}
