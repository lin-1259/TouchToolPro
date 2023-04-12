package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.gson.JsonObject;

import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinObject {
    private final String cls;

    public PinObject() {
        cls = getClass().getName();
    }

    public PinObject(JsonObject jsonObject) {
        cls = GsonUtils.getAsString(jsonObject, "cls", getClass().getName());
    }

    public PinObject copy() {
        return GsonUtils.copy(this, PinObject.class);
    }

    public void setParamValue(String value) {

    }

    public boolean isEmpty() {return true;}

    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryInverse, 0);
    }

    public ShapeAppearanceModel getPinStyle(Context context) {
        int cornerSize = DisplayUtils.dp2px(context, 6);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PinObject pinObject = (PinObject) o;

        return cls.equals(pinObject.cls);
    }

    @Override
    public int hashCode() {
        return cls.hashCode();
    }
}
