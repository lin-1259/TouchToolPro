package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.graphics.Rect;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinColor extends PinValue {
    private int screen;

    private int[] color;
    private int minSize;
    private int maxSize;
    private Rect area;

    public PinColor() {
        super();
        color = new int[]{-1, -1, -1};
        screen = 1080;
        minSize = 0;
        maxSize = 0;
        area = new Rect();
    }

    public PinColor(PinColor pinColor) {
        super();
        color = new int[]{pinColor.color[0], pinColor.color[1], pinColor.color[2]};
        screen = pinColor.screen;
        minSize = pinColor.minSize;
        maxSize = pinColor.maxSize;
        area = new Rect(pinColor.area);
    }

    public PinColor(JsonObject jsonObject) {
        super(jsonObject);
        Gson gson = new Gson();
        color = gson.fromJson(jsonObject.get("color"), int[].class);
        screen = jsonObject.get("screen").getAsInt();
        minSize = jsonObject.get("minSize").getAsInt();
        maxSize = jsonObject.get("maxSize").getAsInt();
        area = gson.fromJson(jsonObject.get("area"), Rect.class);
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

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public Rect getArea(Context context) {
        if (area.left == 0 && area.right == 0 && area.top == 0 && area.bottom == 0) {
            area = DisplayUtils.getScreenArea(context);
            return area;
        }

        float scale = DisplayUtils.getScreen(context) * 1f / screen;
        return new Rect((int) (area.left * scale), (int) (area.top * scale), (int) (area.right * scale), (int) (area.bottom * scale));
    }

    public void setArea(Rect area) {
        this.area = area;
    }
}
