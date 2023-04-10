package top.bogey.touch_tool.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;
import com.tencent.mmkv.MMKV;

import java.util.List;

import top.bogey.touch_tool.MainApplication;

public class SettingSave {
    private static final String RUN_TIMES = "RUN_TIMES";
    private final static String RUNNING_ERROR = "RUNNING_ERROR";

    private static final String SERVICE_ENABLED = "SERVICE_ENABLED";
    private static final String SERVICE_ENABLED_TIP = "SERVICE_ENABLED_TIP";
    private static final String CAPTURE_SERVICE_ENABLED_TIP = "CAPTURE_SERVICE_ENABLED_TIP";

    private final static String PLAY_VIEW_STATE = "PLAY_VIEW_STATE";
    private final static String PLAY_VIEW_POSITION = "PLAY_VIEW_POSITION";
    private final static String PLAY_VIEW_VISIBLE = "PLAY_VIEW_VISIBLE";
    private static final String HIDE_BACKGROUND = "HIDE_BACKGROUND";

    private static final String FIRST_SHOW_TASK = "FIRST_SHOW_TASK";
    private static final String NIGHT_MODE = "NIGHT_MODE";
    private static final String DYNAMIC_COLOR = "DYNAMIC_COLOR";

    private boolean isAppliedDynamicColor = false;
    private boolean isDynamicColor = true;
    private final DynamicColorsOptions options = new DynamicColorsOptions.Builder().setPrecondition((activity, theme) -> isDynamicColor).build();

    private static SettingSave settingSave;
    private static final MMKV settingMMKV = MMKV.defaultMMKV();

    public static SettingSave getInstance() {
        if (settingSave == null) settingSave = new SettingSave();
        return settingSave;
    }

    public void init(Context context) {
        setHideBackground(context, isHideBackground());
        setNightMode(getNightMode());
    }

    public int getRunTimes() {
        return settingMMKV.decodeInt(RUN_TIMES, 0);
    }

    public void addRunTimes() {
        settingMMKV.encode(RUN_TIMES, getRunTimes() + 1);
    }

    public String getRunningError() {
        return settingMMKV.decodeString(RUNNING_ERROR);
    }

    public void setRunningError(String error) {
        settingMMKV.encode(RUNNING_ERROR, error);
    }


    public boolean isServiceEnabled() {
        return settingMMKV.decodeBool(SERVICE_ENABLED, false);
    }

    public void setServiceEnabled(boolean enabled) {
        settingMMKV.encode(SERVICE_ENABLED, enabled);
    }

    public boolean isServiceEnabledTip() {
        return settingMMKV.decodeBool(SERVICE_ENABLED_TIP, false);
    }

    public void setServiceEnabledTip(boolean enabled) {
        settingMMKV.encode(SERVICE_ENABLED_TIP, enabled);
    }

    public boolean isCaptureServiceEnabledTip() {
        return settingMMKV.decodeBool(CAPTURE_SERVICE_ENABLED_TIP, false);
    }

    public void setCaptureServiceEnabledTip(boolean enabled) {
        settingMMKV.encode(CAPTURE_SERVICE_ENABLED_TIP, enabled);
    }


    public boolean isPlayViewExpand() {
        return settingMMKV.decodeBool(PLAY_VIEW_STATE, false);
    }

    public void setPlayViewExpand(boolean expand) {
        settingMMKV.encode(PLAY_VIEW_STATE, expand);
    }

    public boolean isPlayViewVisible() {
        return settingMMKV.decodeBool(PLAY_VIEW_VISIBLE, true);
    }

    public void setPlayViewVisible(boolean visible) {
        settingMMKV.encode(PLAY_VIEW_VISIBLE, visible);
    }

    public Point getPlayViewPosition() {
        return settingMMKV.decodeParcelable(PLAY_VIEW_POSITION, Point.class, new Point());
    }

    public void setPlayViewPosition(Point position) {
        settingMMKV.encode(PLAY_VIEW_POSITION, position);
    }


    public boolean isHideBackground() {
        return settingMMKV.decodeBool(HIDE_BACKGROUND, false);
    }

    public void setHideBackground(Context context, boolean hide) {
        settingMMKV.encode(HIDE_BACKGROUND, hide);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();
            if (tasks != null) tasks.forEach(task -> task.setExcludeFromRecents(hide));
        }
    }


    public int getNightMode() {
        return settingMMKV.decodeInt(NIGHT_MODE, 0);
    }

    public void setNightMode(int nightMode) {
        settingMMKV.encode(NIGHT_MODE, nightMode);
        AppCompatDelegate.setDefaultNightMode(nightMode - 1);
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
                MainApplication.getInstance().getActivity().recreate();
            }
        }
        settingMMKV.encode(DYNAMIC_COLOR, enabled);
    }

    public boolean isFirstShowTask() {
        return settingMMKV.decodeBool(FIRST_SHOW_TASK, false);
    }

    public void setFirstShowTask(boolean firstShowTask) {
        settingMMKV.encode(FIRST_SHOW_TASK, firstShowTask);
    }

}
