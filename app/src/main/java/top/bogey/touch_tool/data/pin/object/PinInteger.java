package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;

public class PinInteger extends PinObject {
    private int value;

    public PinInteger() {
        super();
    }

    public PinInteger(int value) {
        super();
        this.value = value;
    }

    public PinInteger(Parcel in) {
        value = in.readInt();
    }

    @Override
    public int getPinColor(Context context) {
        return context.getResources().getColor(R.color.IntegerPinColor, null);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(value);
    }
}
