package top.bogey.touch_tool.data.pin.object;

import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.NonNull;

public class PinPoint extends PinObject {
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
        x = in.readInt();
        y = in.readInt();
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
