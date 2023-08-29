package top.bogey.touch_tool_pro.bean.action;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;

public enum ActionMap {
    CUSTOM,
    VARIABLE,

    START,
    LOGIC,
    STATE,
    CHECK,
    NORMAL,
    BOOL,
    STRING,
    INT,
    POINT,
    ;

    public String getTitle() {
        String[] array = MainApplication.getInstance().getResources().getStringArray(R.array.action_type);
        return array[ordinal()];
    }

    @NonNull
    public ArrayList<ActionType> getTypes() {
        return switch (this) {
            case START -> new ArrayList<>(Arrays.asList(
                    ActionType.MANUAL_START,
                    ActionType.ENTER_APP_START,
                    ActionType.TIME_START,
                    ActionType.NOTIFY_START,
                    ActionType.BATTERY_START,
                    ActionType.OUTER_START)
            );
            case LOGIC -> new ArrayList<>(Arrays.asList(
                    ActionType.LOGIC_IF,
                    ActionType.LOGIC_WAIT_IF,
                    ActionType.LOGIC_FOR,
                    ActionType.LOGIC_WHILE,
                    ActionType.LOGIC_SEQUENCE,
                    ActionType.LOGIC_RANDOM,
                    ActionType.LOGIC_PARALLEL)
            );
            case STATE -> new ArrayList<>(Arrays.asList(
                    ActionType.APP_STATE,
                    ActionType.BATTERY_STATE,
                    ActionType.SCREEN_STATE,
                    ActionType.CAPTURE_STATE,
                    ActionType.IMAGE_STATE,
                    ActionType.COLOR_STATE,
                    ActionType.DATE_STATE,
                    ActionType.TIME_STATE)
            );
            case CHECK -> new ArrayList<>(Arrays.asList(
                    ActionType.CHECK_EXIST_TEXT,
                    ActionType.CHECK_EXIST_NODE,
                    ActionType.CHECK_EXIST_IMAGE,
                    ActionType.CHECK_EXIST_COLOR,
                    ActionType.CHECK_IMAGE,
                    ActionType.CHECK_COLOR,
                    ActionType.CHECK_IN_APP,
                    ActionType.CHECK_ON_BATTERY_STATE,
                    ActionType.CHECK_ON_SCREEN_STATE)
            );
            case NORMAL -> new ArrayList<>(Arrays.asList(
                    ActionType.DELAY,
                    ActionType.LOG,
                    ActionType.CLICK_POSITION,
                    ActionType.CLICK_NODE,
                    ActionType.CLICK_KEY,
                    ActionType.TOUCH,
                    ActionType.INPUT,
                    ActionType.SCREEN_SWITCH,
                    ActionType.CAPTURE_SWITCH,
                    ActionType.OPEN_APP,
                    ActionType.OPEN_URI,
                    ActionType.PLAY_RINGTONE,
                    ActionType.COPY,
                    ActionType.RUN_TASK,
                    ActionType.BREAK_TASK)
            );
            case BOOL -> new ArrayList<>(Arrays.asList(
                    ActionType.BOOL_OR,
                    ActionType.BOOL_AND,
                    ActionType.BOOL_NOT)
            );
            case STRING -> new ArrayList<>(Arrays.asList(
                    ActionType.STRING_FROM_VALUE,
                    ActionType.STRING_TO_INT,
                    ActionType.STRING_ADD,
                    ActionType.STRING_EQUAL,
                    ActionType.STRING_REGEX)
            );
            case INT -> new ArrayList<>(Arrays.asList(
                    ActionType.INT_ADD,
                    ActionType.INT_REDUCE,
                    ActionType.INT_MULTI,
                    ActionType.INT_DIV,
                    ActionType.INT_MOD,
                    ActionType.INT_EQUAL,
                    ActionType.INT_LARGE,
                    ActionType.INT_SMALL,
                    ActionType.INT_IN_AREA,
                    ActionType.INT_RANDOM)
            );
            case POINT -> new ArrayList<>(Arrays.asList(
                    ActionType.POS_FROM_INT,
                    ActionType.POS_TO_INT,
                    ActionType.POS_OFFSET,
                    ActionType.POS_IN_AREA,
                    ActionType.POS_TO_AREA)
            );
            default -> new ArrayList<>();
        };
    }
}
