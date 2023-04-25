package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.Objects;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.GsonUtils;

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
        value = GsonUtils.getAsString(jsonObject, "value", null);
    }

    @Override
    public void setParamValue(String value) {
        this.value = value;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.StringPinColor);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinString pinString = (PinString) o;

        return Objects.equals(value, pinString.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
