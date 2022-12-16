package top.bogey.touch_tool.data.action.pin;

import android.content.Context;

import top.bogey.touch_tool.utils.DisplayUtils;

public enum PinType {
    EXCUTE,
    BOOLEAN,
    INTEGER,
    STRING,
    DATE,
    TIME,
    PERIODIC,
    TIME_AREA,
    APP,
    ARRAY;

    public int getPinColor(Context context) {
        switch (this) {
            case BOOLEAN:
                return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorError, 0);
            case EXCUTE:
                return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
        }
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0);
    }
}
