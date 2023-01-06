package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinBoolean extends PinObject {
    private boolean value;

    public PinBoolean() {
        super();
    }

    public PinBoolean(boolean value) {
        super();
        this.value = value;
    }

    public PinBoolean(Parcel in) {
        value = in.readByte() == 1;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorError, 0);
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (value ? 1 : 0));
    }
}
