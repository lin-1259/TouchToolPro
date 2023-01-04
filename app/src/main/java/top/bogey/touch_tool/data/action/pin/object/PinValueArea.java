package top.bogey.touch_tool.data.action.pin.object;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class PinValueArea extends PinObject{
    private final int valueFrom;
    private final int valueTo;
    private final int step;

    private int currMin;
    private int currMax;

    public PinValueArea(int valueFrom, int valueTo, int step) {
        super();
        this.valueFrom = valueFrom;
        currMin = valueFrom;
        this.valueTo = valueTo;
        currMax = valueTo;
        this.step = step;
    }

    public PinValueArea(Parcel in) {
        valueFrom = in.readInt();
        valueTo = in.readInt();
        step = in.readInt();
        currMin = in.readInt();
        currMax = in.readInt();
    }

    public int getValueFrom() {
        return valueFrom;
    }

    public int getValueTo() {
        return valueTo;
    }

    public int getStep() {
        return step;
    }

    public int getCurrMin() {
        return currMin;
    }

    public void setCurrMin(int currMin) {
        this.currMin = currMin;
    }

    public int getCurrMax() {
        return currMax;
    }

    public void setCurrMax(int currMax) {
        this.currMax = currMax;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(valueFrom);
        dest.writeInt(valueTo);
        dest.writeInt(step);
        dest.writeInt(currMin);
        dest.writeInt(currMax);
    }
}
