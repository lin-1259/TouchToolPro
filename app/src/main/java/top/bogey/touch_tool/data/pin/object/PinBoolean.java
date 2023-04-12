package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.GsonUtils;

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
        value = GsonUtils.getAsBoolean(jsonObject, "value", false);
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
    public boolean isEmpty() {
        return false;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinBoolean that = (PinBoolean) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value ? 1 : 0);
        return result;
    }
}
