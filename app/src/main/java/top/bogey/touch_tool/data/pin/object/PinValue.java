package top.bogey.touch_tool.data.pin.object;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class PinValue extends PinObject{

    public PinValue() {
        super();
    }

    public PinValue(Parcel in) {
        super(in);
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }
}
