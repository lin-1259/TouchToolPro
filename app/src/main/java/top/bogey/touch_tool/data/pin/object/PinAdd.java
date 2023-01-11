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
        byte[] bytes = in.createByteArray();
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        pin = Pin.CREATOR.createFromParcel(parcel);
        parcel.recycle();
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
        Parcel parcel = Parcel.obtain();
        pin.writeToParcel(parcel, flags);
        dest.writeByteArray(parcel.marshall());
        parcel.recycle();
    }
}
