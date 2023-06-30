package top.bogey.touch_tool.data.pin.object;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinArea extends PinValue {
    public int left;
    public int top;
    public int right;
    public int bottom;
    private int screen;

    public PinArea() {
        super();
        screen = 1080;
    }

    public PinArea(int left, int top, int right, int bottom, int screen) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.screen = screen;
    }

    public PinArea(JsonObject jsonObject) {
        super(jsonObject);
        left = GsonUtils.getAsInt(jsonObject, "left", 0);
        top = GsonUtils.getAsInt(jsonObject, "top", 0);
        right = GsonUtils.getAsInt(jsonObject, "right", 0);
        bottom = GsonUtils.getAsInt(jsonObject, "bottom", 0);
        screen = GsonUtils.getAsInt(jsonObject, "screen", 0);
    }

    public void setArea(Context context, int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        screen = DisplayUtils.getScreen(context);
    }

    public void setArea(Context context, PinArea area) {
        setArea(context, area.left, area.top, area.right, area.bottom);
    }

    public void setArea(Context context, Rect area) {
        setArea(context, area.left, area.top, area.right, area.bottom);
    }

    public Rect getArea(Context context){
        if (isEmpty()) return DisplayUtils.getScreenArea(context);
        float scale = DisplayUtils.getScreen(context) * 1f / screen;
        return new Rect((int) (left * scale), (int) (top * scale), (int) (right * scale), (int) (bottom * scale));
    }

    @Override
    public boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.AreaPinColor);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("(←%d, ↑%d, →%d, ↓%d)", left, top, right, bottom);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinArea pinArea = (PinArea) o;

        if (left != pinArea.left) return false;
        if (top != pinArea.top) return false;
        if (right != pinArea.right) return false;
        return bottom == pinArea.bottom;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + left;
        result = 31 * result + top;
        result = 31 * result + right;
        result = 31 * result + bottom;
        return result;
    }
}
