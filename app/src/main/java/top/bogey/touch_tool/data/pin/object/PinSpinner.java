package top.bogey.touch_tool.data.pin.object;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.GsonUtils;

public class PinSpinner extends PinValue {
    private final String[] array;
    private int index;

    public PinSpinner(String[] array) {
        super();
        this.array = array;
    }

    public PinSpinner(JsonObject jsonObject) {
        super(jsonObject);
        array = GsonUtils.getAsClass(jsonObject, "array", String[].class, new String[]{});
        index = GsonUtils.getAsInt(jsonObject, "index", 0);
    }

    public String[] getArrays() {
        return array;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
