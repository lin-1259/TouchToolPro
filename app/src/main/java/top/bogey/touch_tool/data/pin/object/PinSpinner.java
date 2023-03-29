package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.ArrayRes;
import androidx.annotation.StringRes;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.GsonUtils;

public class PinSpinner extends PinValue {
    private transient int array;
    private int index;

    public PinSpinner(@ArrayRes int array) {
        super();
        this.array = array;
    }

    public PinSpinner(JsonObject jsonObject) {
        super(jsonObject);
        index = GsonUtils.getAsInt(jsonObject, "index", 0);
    }

    public String[] getArrays(Context context) {
        if (array == 0) return new String[]{};
        return context.getResources().getStringArray(array);
    }

    public int getArray() {
        return array;
    }

    public void setArray(@ArrayRes int array) {
        this.array = array;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
