package top.bogey.touch_tool.ui.setting;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.tencent.mmkv.MMKV;

import top.bogey.touch_tool.MainApplication;

public class SettingSave {
    private final static String RUN_TIMES = "RUN_TIMES";
    private static final String SERVICE_ENABLED = "SERVICE_ENABLED";
    private final static String SORT_TYPE = "SORT_TYPE";

    private final static String ACTION_TOUCH_OFFSET = "ACTION_TOUCH_OFFSET";
    private final static String ACTION_RECORD_DELAY = "ACTION_RECORD_DELAY";

    private final static String NIGHT_MODE = "NIGHT_MODE";
    private final static String DYNAMIC_COLOR = "DYNAMIC_COLOR";

    private static SettingSave settingSave;

    private boolean isAppliedDynamicColor = false;
    private boolean isDynamicColor = true;
    private final DynamicColorsOptions options = new DynamicColorsOptions.Builder().setPrecondition((activity, theme) -> isDynamicColor).build();

    private final MMKV settingMMKV;

    public static SettingSave getInstance() {
        if (settingSave == null) settingSave = new SettingSave();
        return settingSave;
    }

    public SettingSave() {
        settingMMKV = MMKV.defaultMMKV();
    }


    public int getRunTimes() {
        return settingMMKV.decodeInt(RUN_TIMES, 0);
    }

    public void addRunTimes() {
        settingMMKV.encode(RUN_TIMES, getRunTimes() + 1);
    }


    public boolean isServiceEnabled() {
        return settingMMKV.decodeBool(SERVICE_ENABLED, false);
    }

    public void setServiceEnabled(boolean enabled) {
        settingMMKV.encode(SERVICE_ENABLED, enabled);
    }


    public int getActionTouchOffset() {
        return settingMMKV.decodeInt(ACTION_TOUCH_OFFSET, 10);
    }

    public void setActionTouchOffset(int offset) {
        settingMMKV.encode(ACTION_TOUCH_OFFSET, offset);
    }

    public int getTouchOffset(boolean offset, int value) {
        if (!offset) return value;
        int touchOffset = getActionTouchOffset();
        return (int) (Math.random() * touchOffset * 2 + value - touchOffset);
    }

    public int getActionRecordDelay() {
        return settingMMKV.decodeInt(ACTION_RECORD_DELAY, 300);
    }

    public void setActionRecordDelay(int delay) {
        settingMMKV.encode(ACTION_RECORD_DELAY, delay);
    }


    public boolean isDynamicColor() {
        return settingMMKV.decodeBool(DYNAMIC_COLOR, isDynamicColor);
    }

    public void setDynamicColor(Context context, boolean enabled) {
        if (!isAppliedDynamicColor) {
            isAppliedDynamicColor = true;
            isDynamicColor = enabled;
            DynamicColors.applyToActivitiesIfAvailable((Application) context.getApplicationContext(), options);
        } else {
            if (isDynamicColor != enabled) {
                isDynamicColor = enabled;
                MainApplication.getActivity().recreate();
            }
        }
        settingMMKV.encode(DYNAMIC_COLOR, enabled);
    }
}
