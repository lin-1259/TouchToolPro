package top.bogey.touch_tool.data.pin.object;

import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public class PinTimeArea extends PinValue {
    private int min;
    private int max;
    private TimeUnit unit;

    public PinTimeArea(int time, TimeUnit unit) {
        this(time, time, unit);
    }

    public PinTimeArea(int min, int max, TimeUnit unit) {
        super();
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public PinTimeArea(Parcel in) {
        min = in.readInt();
        max = in.readInt();
        unit = TimeUnit.values()[in.readByte()];
    }

    public void setTime(int time, TimeUnit unit) {
        setTime(time, time, unit);
    }

    public void setTime(int min, int max, TimeUnit unit) {
        this.min = min;
        this.max = max;
        this.unit = unit;
    }

    public int getRandomTime() {
        return (int) unit.toMillis(Math.round(Math.random() * Math.abs(max - min) + getMin()));
    }

    public int getMin() {
        return Math.min(min, max);
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return Math.max(min, max);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeByte((byte) unit.ordinal());
    }
}
