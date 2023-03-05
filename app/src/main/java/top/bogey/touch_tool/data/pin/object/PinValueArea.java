package top.bogey.touch_tool.data.pin.object;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

public class PinValueArea extends PinValue {
    private final int valueFrom;
    private final int valueTo;
    private final int step;

    private int currMin;
    private int currMax;

    public PinValueArea() {
        this(1, 60000, 1);
    }

    public PinValueArea(int valueFrom, int valueTo, int step) {
        this(valueFrom, valueTo, step, valueFrom, valueTo);
    }

    public PinValueArea(int valueFrom, int valueTo, int step, int currMin, int currMax) {
        super();
        if ((valueTo - valueFrom) * 1f % step != 0) throw new RuntimeException("步长有问题");
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.step = step;
        this.currMin = currMin;
        this.currMax = currMax;
    }

    public PinValueArea(JsonObject jsonObject) {
        super(jsonObject);
        valueFrom = jsonObject.get("valueFrom").getAsInt();
        valueTo = jsonObject.get("valueTo").getAsInt();
        step = jsonObject.get("step").getAsInt();
        currMin = jsonObject.get("currMin").getAsInt();
        currMax = jsonObject.get("currMax").getAsInt();
    }

    public int getRandomValue() {
        return (int) (currMin + (currMax - currMin) * Math.random());
    }

    public int getValueFrom() {
        return valueFrom;
    }

    public int getValueTo() {
        return valueTo;
    }

    public int getCurrMin() {
        return currMin;
    }

    public void setCurrMin(int currMin) {
        currMin = Math.max(valueFrom, Math.min(valueTo, currMin));

        this.currMin = (currMin - valueFrom) / step * step + valueFrom;
    }

    public int getCurrMax() {
        return currMax;
    }

    public void setCurrMax(int currMax) {
        currMax = Math.max(valueFrom, Math.min(valueTo, currMax));

        currMax = (currMax - valueFrom) / step * step + valueFrom;
        this.currMax = Math.min(currMax, valueTo);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("(%d - %d)", currMin, currMax);
    }
}
