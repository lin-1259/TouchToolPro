package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinBoolean extends PinValue {
    private boolean value;

    public PinBoolean() {
        super();
    }

    public PinBoolean(boolean value) {
        super();
        this.value = value;
    }

    public PinBoolean(JsonObject jsonObject) {
        super(jsonObject);
        value = jsonObject.get("value").getAsBoolean();
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorError, 0);
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
