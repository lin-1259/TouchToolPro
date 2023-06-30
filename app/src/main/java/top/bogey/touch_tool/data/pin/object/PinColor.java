package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.Arrays;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinColor extends PinValue {
    private int screen;

    private int[] color;
    private int minSize;
    private int maxSize;

    public PinColor() {
        super();
        color = new int[]{-1, -1, -1};
        screen = 1080;
        minSize = 0;
        maxSize = 0;
    }

    public PinColor(JsonObject jsonObject) {
        super(jsonObject);
        color = GsonUtils.getAsClass(jsonObject, "color", int[].class, new int[]{-1, -1, -1});
        screen = GsonUtils.getAsInt(jsonObject, "screen", 1080);
        minSize = GsonUtils.getAsInt(jsonObject, "minSize", 0);
        maxSize = GsonUtils.getAsInt(jsonObject, "maxSize", 0);
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

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.ColorPinColor);
    }

    @Override
    public boolean isEmpty() {
        for (int i : color) {
            if (i < 0) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinColor pinColor = (PinColor) o;

        if (screen != pinColor.screen) return false;
        if (minSize != pinColor.minSize) return false;
        if (maxSize != pinColor.maxSize) return false;
        return Arrays.equals(color, pinColor.color);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + screen;
        result = 31 * result + Arrays.hashCode(color);
        result = 31 * result + minSize;
        result = 31 * result + maxSize;
        return result;
    }
}
