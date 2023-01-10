package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinColor extends PinValue {
    private int[] color;
    private int screen;
    private int minSize;
    private int maxSize;

    public PinColor() {
        super();
        color = new int[]{-1, -1, -1};
        screen = 1080;
        minSize = 0;
        maxSize = 0;
    }

    public PinColor(Parcel in) {
        super(in);
        color = new int[]{-1, -1, -1};
        in.readIntArray(color);
        screen = in.readInt();
        minSize = in.readInt();
        maxSize = in.readInt();
    }

    public boolean isValid() {
        for (int i : color) {
            if (i < 0) return false;
        }
        return true;
    }

    public int getMinSize(Context context) {
        int screen = DisplayUtils.getScreen(context);
        if (screen == this.screen) return minSize;
        else {
            float scale = screen * 1f / this.screen;
            return (int) (scale * minSize);
        }
    }

    public int getMaxSize(Context context) {
        int screen = DisplayUtils.getScreen(context);
        if (screen == this.screen) return maxSize;
        else {
            float scale = screen * 1f / this.screen;
            return (int) (scale * maxSize);
        }
    }

    public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
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
