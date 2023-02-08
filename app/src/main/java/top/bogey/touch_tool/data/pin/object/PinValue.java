package top.bogey.touch_tool.data.pin.object;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

public class PinValue extends PinObject {

    public PinValue() {
        super();
    }

    public PinValue(JsonObject jsonObject) {
        super(jsonObject);
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }
}
