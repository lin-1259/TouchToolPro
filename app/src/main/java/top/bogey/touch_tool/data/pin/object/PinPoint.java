package top.bogey.touch_tool.data.pin.object;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.GsonUtils;

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

    public PinPoint(JsonObject jsonObject) {
        super(jsonObject);
        x = GsonUtils.getAsInt(jsonObject, "x", 0);
        y = GsonUtils.getAsInt(jsonObject, "y", 0);
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
    public boolean isEmpty() {
        return x == 0 && y == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinPoint pinPoint = (PinPoint) o;

        if (x != pinPoint.x) return false;
        return y == pinPoint.y;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
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
}
