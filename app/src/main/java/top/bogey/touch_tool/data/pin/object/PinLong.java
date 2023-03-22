package top.bogey.touch_tool.data.pin.object;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.GsonUtils;

public class PinLong extends PinValue {
    private long value;

    public PinLong(long value) {
        super();
        this.value = value;
    }

    public PinLong(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsLong(jsonObject, "value", 0);
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
