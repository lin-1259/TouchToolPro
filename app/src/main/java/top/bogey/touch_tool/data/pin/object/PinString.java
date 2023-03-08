package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;

public class PinString extends PinValue {
    private String value;

    public PinString() {
        super();
    }

    public PinString(String value) {
        super();
        this.value = value;
    }

    public PinString(JsonObject jsonObject) {
        super(jsonObject);
        JsonElement element = jsonObject.get("value");
        if (element != null) value = element.getAsString();
    }

    @Override
    public void setParamValue(String value) {
        this.value = value;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getResources().getColor(R.color.StringPinColor, null);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
