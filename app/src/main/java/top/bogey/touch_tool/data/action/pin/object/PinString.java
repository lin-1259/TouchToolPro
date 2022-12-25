package top.bogey.touch_tool.data.action.pin.object;

import android.os.Parcel;

import androidx.annotation.NonNull;

public class PinString extends PinObject {
    private String value;

    public PinString() {
        super();
    }

    public PinString(String value) {
        super();
        this.value = value;
    }

    public PinString(Parcel in) {
        value = in.readString();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(value);
    }
}
