package top.bogey.touch_tool.data.action.pin;

import android.content.Context;

public class PinSpinnerHelper {
    private int array;
    private String[] arrays;
    private int index;

    public PinSpinnerHelper(int array) {
        this.array = array;
    }

    public PinSpinnerHelper(String[] arrays) {
        this.arrays = arrays;
    }

    public String[] getArrays(Context context) {
        if (array == 0) return arrays;
        return context.getResources().getStringArray(array);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
