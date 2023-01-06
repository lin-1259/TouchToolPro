package top.bogey.touch_tool.data.pin;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public enum PinDirection implements Parcelable {
    IN, OUT;

    public static final Creator<PinDirection> CREATOR = new Creator<PinDirection>() {
        @Override
        public PinDirection createFromParcel(Parcel in) {
            return PinDirection.valueOf(in.readString());
        }

        @Override
        public PinDirection[] newArray(int size) {
            return new PinDirection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name());
    }
}
