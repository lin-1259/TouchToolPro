package top.bogey.touch_tool.utils;

import com.tencent.mmkv.MMKV;

public class SettingSave {
    private static final String RUN_TIMES = "RUN_TIMES";
    private final static String RUNNING_ERROR = "RUNNING_ERROR";

    private static final String SERVICE_ENABLED = "SERVICE_ENABLED";
    private static final String SERVICE_ENABLED_TIP = "SERVICE_ENABLED_TIP";
    private static final String CAPTURE_SERVICE_ENABLED_TIP = "CAPTURE_SERVICE_ENABLED_TIP";

    private final static String PLAY_VIEW_STATE = "PLAY_VIEW_STATE";
    private final static String PLAY_VIEW_VISIBLE = "PLAY_VIEW_VISIBLE";

    private static SettingSave settingSave;
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

    public void setPlayViewVisible(boolean expand) {
        settingMMKV.encode(PLAY_VIEW_VISIBLE, expand);
    }
}
