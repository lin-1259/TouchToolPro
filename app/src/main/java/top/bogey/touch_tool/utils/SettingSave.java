package top.bogey.touch_tool.utils;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Arrays;

import top.bogey.touch_tool.R;

public class SettingSave {
    private static final String RUN_TIMES = "RUN_TIMES";

    private static final String SERVICE_ENABLED = "SERVICE_ENABLED";

    private final static String PLAY_VIEW_STATE = "PLAY_VIEW_STATE";

    private static final String TAGS = "TAGS";

    private static SettingSave settingSave;
    private final MMKV settingMMKV;
    private final MMKV tagsMMKV;

    public static SettingSave getInstance() {
        if (settingSave == null) settingSave = new SettingSave();
        return settingSave;
    }

    public SettingSave() {
        settingMMKV = MMKV.defaultMMKV();
        tagsMMKV = MMKV.mmkvWithID(TAGS, MMKV.SINGLE_PROCESS_MODE);
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

    public ArrayList<String> getTags(Context context) {
        String[] keys = tagsMMKV.allKeys();
        ArrayList<String> tags = new ArrayList<>(Arrays.asList(context.getString(R.string.tag_all), context.getString(R.string.tag_no)));
        if (keys != null) {
            for (int i = keys.length - 1; i >= 0; i--) {
                String key = keys[i];
                tags.add(tags.size() - 1, key);
            }
        }
        return tags;
    }

    public void addTag(String tag) {
        tagsMMKV.encode(tag, true);
    }

    public void removeTag(String tag) {
        tagsMMKV.remove(tag);
    }


    public boolean isPlayViewExpand() {
        return settingMMKV.decodeBool(PLAY_VIEW_STATE, false);
    }

    public void setPlayViewExpand(boolean expand) {
        settingMMKV.encode(PLAY_VIEW_STATE, expand);
    }
}
