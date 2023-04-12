package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinInteger extends PinValue {
    private int value;

    public PinInteger() {
        super();
        value = 0;
    }

    public PinInteger(int value) {
        super();
        this.value = value;
    }

    public PinInteger(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsInt(jsonObject, "value", 0);
    }

    @Override
    public void setParamValue(String value) {
        try {
            this.value = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {}
    }

    @Override
    public int getPinColor(Context context) {
        return context.getResources().getColor(R.color.IntegerPinColor, null);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return value == 0;
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

        PinInteger that = (PinInteger) o;

        return value == that.value;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value;
        return result;
    }
}
