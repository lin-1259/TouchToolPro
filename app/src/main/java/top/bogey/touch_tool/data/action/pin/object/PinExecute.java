package top.bogey.touch_tool.data.action.pin.object;

import android.content.Context;
import android.os.Parcel;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.utils.DisplayUtils;

public class PinExecute extends PinObject {

    public PinExecute() {
        super();
    }

    public PinExecute(Parcel in) {
    }

    @Override
    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
