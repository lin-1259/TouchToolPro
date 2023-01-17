package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

public class PinSpinner extends PinValue {
    private final int array;
    private int index;

    public PinSpinner(int array) {
        super();
        this.array = array;
    }

    public PinSpinner(Parcel in) {
        super(in);
        array = in.readInt();
        index = in.readInt();
    }

    public String[] getArrays(Context context) {
        return context.getResources().getStringArray(array);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(array);
        dest.writeInt(index);
    }
}
