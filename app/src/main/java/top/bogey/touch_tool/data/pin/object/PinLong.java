package top.bogey.touch_tool.data.pin.object;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class PinLong extends PinObject{
    private long value;

    public PinLong() {
        super();
    }

    public PinLong(long value) {
        super();
        this.value = value;
    }

    public PinLong(Parcel in) {
        value = in.readLong();
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(value);
    }
}
