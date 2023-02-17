package top.bogey.touch_tool.data.pin.object;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PinSpinner extends PinValue {
    private final String[] array;
    private int index;

    public PinSpinner() {
        this(new String[]{});
    }

    public PinSpinner(String[] array) {
        super();
        this.array = array;
    }

    public PinSpinner(JsonObject jsonObject) {
        super(jsonObject);
        array = new Gson().fromJson(jsonObject.get("array"), String[].class);
        index = jsonObject.get("index").getAsInt();
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
