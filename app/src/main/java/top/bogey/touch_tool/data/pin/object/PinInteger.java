package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;

public class PinInteger extends PinValue {
    private int value;

    public PinInteger() {
        super();
    }

    public PinInteger(int value) {
        super();
        this.value = value;
    }

    public PinInteger(JsonObject jsonObject) {
        super(jsonObject);
        value = jsonObject.get("value").getAsInt();
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
