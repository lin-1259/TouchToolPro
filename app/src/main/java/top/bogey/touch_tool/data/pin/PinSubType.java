package top.bogey.touch_tool.data.pin;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public enum PinSubType implements Parcelable {
    NORMAL,
    DATE,
    TIME,
    PERIODIC,
    ID,
    LEVEL;

    public static final Creator<PinSubType> CREATOR = new Creator<PinSubType>() {
        @Override
        public PinSubType createFromParcel(Parcel in) {
            return PinSubType.valueOf(in.readString());
        }

        @Override
        public PinSubType[] newArray(int size) {
            return new PinSubType[size];
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
