package top.bogey.touch_tool.data.pin.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.SettingSave;

public class PinPoint extends PinValue {
    private int x;
    private int y;

    public PinPoint() {
        super();
    }

    public PinPoint(int x, int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public PinPoint(Parcel in) {
        super(in);
        x = in.readInt();
        y = in.readInt();
    }

    public int getX(boolean offset) {
        return getOffsetValue(offset, x);
    }

    public int getY(boolean offset) {
        return getOffsetValue(offset, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public static int getOffsetValue(boolean offset, int value) {
        if (!offset) return value;
        return (int) (Math.random() * 20 + value - 10);
    }

    @Override
    public int getPinColor(Context context) {
        return context.getResources().getColor(R.color.PointPinColor, null);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(x);
        dest.writeInt(y);
    }
}
