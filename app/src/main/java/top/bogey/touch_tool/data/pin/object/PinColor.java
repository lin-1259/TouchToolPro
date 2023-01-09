package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinColor extends PinValue {
    private final int[] color;
    private final int screen;
    private final int minSize;
    private final int maxSize;

    public PinColor() {
        super();
        color = new int[]{-1, -1, -1};
        screen = 1080;
        minSize = 0;
        maxSize = 0;
    }

    public PinColor(Context context, int[] color, int minSize, int maxSize) {
        screen = DisplayUtils.getScreen(context);
        this.color = color;
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public PinColor(Parcel in) {
        super(in);
        color = new int[]{-1, -1, -1};
        in.readIntArray(color);
        screen = in.readInt();
        minSize = in.readInt();
        maxSize = in.readInt();
    }

    public int[] getColor() {
        return color;
    }

    public int getScreen() {
        return screen;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeIntArray(color);
        dest.writeInt(screen);
        dest.writeInt(minSize);
        dest.writeInt(maxSize);
    }
}
