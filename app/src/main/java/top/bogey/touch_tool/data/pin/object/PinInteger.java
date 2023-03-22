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

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
