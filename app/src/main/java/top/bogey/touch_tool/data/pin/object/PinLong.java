package top.bogey.touch_tool.data.pin.object;

import com.google.gson.JsonObject;

public class PinLong extends PinValue {
    private long value;

    public PinLong() {
        super();
    }

    public PinLong(long value) {
        super();
        this.value = value;
    }

    public PinLong(JsonObject jsonObject) {
        super(jsonObject);
        value = jsonObject.get("value").getAsLong();
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
