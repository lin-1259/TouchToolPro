package top.bogey.touch_tool.data.pin;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public enum PinSlotType implements Parcelable {
    SINGLE, MULTI;

    public static final Creator<PinSlotType> CREATOR = new Creator<PinSlotType>() {
        @Override
        public PinSlotType createFromParcel(Parcel in) {
            return PinSlotType.valueOf(in.readString());
        }

        @Override
        public PinSlotType[] newArray(int size) {
            return new PinSlotType[size];
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
