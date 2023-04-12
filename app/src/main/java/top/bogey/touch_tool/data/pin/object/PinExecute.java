package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinExecute extends PinObject {

    public PinExecute() {
        super();
    }

    public PinExecute(JsonObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
    }

    @Override
    public ShapeAppearanceModel getPinStyle(Context context) {
        float cornerSize = DisplayUtils.dp2px(context, 5.5f);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.CUT, 0)
                .setTopRightCorner(CornerFamily.CUT, 0)
                .setBottomLeftCorner(CornerFamily.CUT, cornerSize)
                .setBottomRightCorner(CornerFamily.CUT, cornerSize)
                .build();
    }
}
