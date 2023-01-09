package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinAdd extends PinObject {
    private final Pin<? extends PinObject> pin;

    public PinAdd(Pin<? extends PinObject> pin) {
        super();
        this.pin = pin;
    }

    public PinAdd(Parcel in) {
        super(in);
        pin = in.readParcelable(Pin.class.getClassLoader());
    }

    public Pin<? extends PinObject> getPin() {
        return pin;
    }

    @NonNull
    @Override
    public String toString() {
        return "";
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(pin, flags);
    }
}
