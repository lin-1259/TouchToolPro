package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinExecute extends PinObject {

    public PinExecute() {
        super();
    }

    public PinExecute(Parcel in) {
        super(in);
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
    }

    @Override
    public ShapeAppearanceModel getPinStyle(Context context) {
        int cornerSize = DisplayUtils.dp2px(context, 5.5f);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.CUT, 0)
                .setTopRightCorner(CornerFamily.CUT, 0)
                .setBottomLeftCorner(CornerFamily.CUT, cornerSize)
                .setBottomRightCorner(CornerFamily.CUT, cornerSize)
                .build();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
