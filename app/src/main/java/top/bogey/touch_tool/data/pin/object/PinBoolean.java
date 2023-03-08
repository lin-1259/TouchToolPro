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

    @Override
    public void setParamValue(String value) {
        if (Boolean.toString(true).equals(value)) this.value = true;
        else if (Boolean.toString(false).equals(value)) this.value = false;
        else this.value = !"0".equals(value);
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
