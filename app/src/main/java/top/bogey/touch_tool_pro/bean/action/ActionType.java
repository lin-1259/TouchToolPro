package top.bogey.touch_tool_pro.bean.action;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.array.ArrayAddAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayAppendAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayClearAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayForLogicAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayGetAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayIndexOfAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayInsertAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayMakeAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayRemoveAction;
import top.bogey.touch_tool_pro.bean.action.array.ArraySetAction;
import top.bogey.touch_tool_pro.bean.action.array.ArraySizeAction;
import top.bogey.touch_tool_pro.bean.action.array.ArrayValidIndexAction;
import top.bogey.touch_tool_pro.bean.action.bool.BoolAndAction;
import top.bogey.touch_tool_pro.bean.action.bool.BoolNotAction;
import top.bogey.touch_tool_pro.bean.action.bool.BoolOrAction;
import top.bogey.touch_tool_pro.bean.action.color.ColorEqualAction;
import top.bogey.touch_tool_pro.bean.action.color.ColorStateAction;
import top.bogey.touch_tool_pro.bean.action.color.ExistColorAction;
import top.bogey.touch_tool_pro.bean.action.color.ExistColorsAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionEndAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.function.FunctionStartAction;
import top.bogey.touch_tool_pro.bean.action.image.ExistImageAction;
import top.bogey.touch_tool_pro.bean.action.image.ImageContainAction;
import top.bogey.touch_tool_pro.bean.action.image.ImageStateAction;
import top.bogey.touch_tool_pro.bean.action.image.SubImageAction;
import top.bogey.touch_tool_pro.bean.action.logic.ForLogicAction;
import top.bogey.touch_tool_pro.bean.action.logic.IfLogicAction;
import top.bogey.touch_tool_pro.bean.action.logic.ManualChoiceAction;
import top.bogey.touch_tool_pro.bean.action.logic.ParallelAction;
import top.bogey.touch_tool_pro.bean.action.logic.RandomAction;
import top.bogey.touch_tool_pro.bean.action.logic.SequenceAction;
import top.bogey.touch_tool_pro.bean.action.logic.WaitIfLogicAction;
import top.bogey.touch_tool_pro.bean.action.logic.WhileLogicAction;
import top.bogey.touch_tool_pro.bean.action.node.ExistNodeAction;
import top.bogey.touch_tool_pro.bean.action.node.ExistNodesAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodeChildrenAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodeInfoStateAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodeParentAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodesInPosAction;
import top.bogey.touch_tool_pro.bean.action.node.GetNodesInWindowAction;
import top.bogey.touch_tool_pro.bean.action.node.IsNodeValidAction;
import top.bogey.touch_tool_pro.bean.action.normal.BreakTaskAction;
import top.bogey.touch_tool_pro.bean.action.normal.CaptureSwitchAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickKeyAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickNodeAction;
import top.bogey.touch_tool_pro.bean.action.normal.ClickPositionAction;
import top.bogey.touch_tool_pro.bean.action.normal.CopyToClipboardAction;
import top.bogey.touch_tool_pro.bean.action.normal.DelayAction;
import top.bogey.touch_tool_pro.bean.action.normal.InputAction;
import top.bogey.touch_tool_pro.bean.action.normal.LogAction;
import top.bogey.touch_tool_pro.bean.action.normal.OpenAppAction;
import top.bogey.touch_tool_pro.bean.action.normal.OpenUriAction;
import top.bogey.touch_tool_pro.bean.action.normal.PlayRingtoneAction;
import top.bogey.touch_tool_pro.bean.action.normal.RunTaskAction;
import top.bogey.touch_tool_pro.bean.action.normal.ScreenSwitchAction;
import top.bogey.touch_tool_pro.bean.action.normal.ShareAction;
import top.bogey.touch_tool_pro.bean.action.normal.ShellAction;
import top.bogey.touch_tool_pro.bean.action.normal.SniPasteAction;
import top.bogey.touch_tool_pro.bean.action.normal.StopRingtoneAction;
import top.bogey.touch_tool_pro.bean.action.normal.TouchAction;
import top.bogey.touch_tool_pro.bean.action.number.IntAddAction;
import top.bogey.touch_tool_pro.bean.action.number.IntDivAction;
import top.bogey.touch_tool_pro.bean.action.number.IntEqualAction;
import top.bogey.touch_tool_pro.bean.action.number.IntInAreaAction;
import top.bogey.touch_tool_pro.bean.action.number.IntLargeAction;
import top.bogey.touch_tool_pro.bean.action.number.IntModAction;
import top.bogey.touch_tool_pro.bean.action.number.IntMultiAction;
import top.bogey.touch_tool_pro.bean.action.number.IntRandomAction;
import top.bogey.touch_tool_pro.bean.action.number.IntReduceAction;
import top.bogey.touch_tool_pro.bean.action.number.IntSmallAction;
import top.bogey.touch_tool_pro.bean.action.number.IntToValueAreaAction;
import top.bogey.touch_tool_pro.bean.action.other.AppStateAction;
import top.bogey.touch_tool_pro.bean.action.other.BatteryStateAction;
import top.bogey.touch_tool_pro.bean.action.other.CaptureStateAction;
import top.bogey.touch_tool_pro.bean.action.other.DateStateAction;
import top.bogey.touch_tool_pro.bean.action.other.InAppCheckAction;
import top.bogey.touch_tool_pro.bean.action.other.NetworkCheckAction;
import top.bogey.touch_tool_pro.bean.action.other.NetworkStateAction;
import top.bogey.touch_tool_pro.bean.action.other.OnBatteryStateAction;
import top.bogey.touch_tool_pro.bean.action.other.OnScreenStateAction;
import top.bogey.touch_tool_pro.bean.action.other.ScreenStateAction;
import top.bogey.touch_tool_pro.bean.action.other.TimeStateAction;
import top.bogey.touch_tool_pro.bean.action.pos.AreaPickAction;
import top.bogey.touch_tool_pro.bean.action.pos.AreaToIntAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosFromIntAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosInAreaAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosOffsetAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosToAreaAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosToIntAction;
import top.bogey.touch_tool_pro.bean.action.pos.PosToTouchAction;
import top.bogey.touch_tool_pro.bean.action.start.AppStartAction;
import top.bogey.touch_tool_pro.bean.action.start.BatteryStartAction;
import top.bogey.touch_tool_pro.bean.action.start.ManualStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NetworkStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NormalStartAction;
import top.bogey.touch_tool_pro.bean.action.start.NotifyStartAction;
import top.bogey.touch_tool_pro.bean.action.start.OuterStartAction;
import top.bogey.touch_tool_pro.bean.action.start.TimeStartAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextOcrAction;
import top.bogey.touch_tool_pro.bean.action.string.ExistTextsAction;
import top.bogey.touch_tool_pro.bean.action.string.OcrTextStateAction;
import top.bogey.touch_tool_pro.bean.action.string.StringAddAction;
import top.bogey.touch_tool_pro.bean.action.string.StringEqualAction;
import top.bogey.touch_tool_pro.bean.action.string.StringFromValueAction;
import top.bogey.touch_tool_pro.bean.action.string.StringRegexAction;
import top.bogey.touch_tool_pro.bean.action.string.StringToIntAction;
import top.bogey.touch_tool_pro.bean.action.var.GetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.GetLocalVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetLocalVariableValue;

public enum ActionType {
    BASE,

    CUSTOM,
    CUSTOM_START,
    CUSTOM_END,
    CUSTOM_PIN,

    VAR_GET,
    VAR_SET,
    COMMON_VAR_GET,
    COMMON_VAR_SET,

    MANUAL_START,
    ENTER_APP_START,
    TIME_START,
    NOTIFY_START,
    NETWORK_START,
    BATTERY_START,
    OUTER_START,
    NORMAL_START,
    INNER_START,

    LOGIC_IF,
    LOGIC_WAIT_IF,
    LOGIC_FOR,
    LOGIC_WHILE,
    LOGIC_SEQUENCE,
    LOGIC_RANDOM,
    LOGIC_PARALLEL,
    LOGIC_MANUAL_CHOICE,

    DELAY,
    LOG,
    CLICK_POSITION,
    CLICK_NODE,
    CLICK_KEY,
    TOUCH,
    INPUT,
    SCREEN_SWITCH,
    CAPTURE_SWITCH,
    OPEN_APP,
    OPEN_URI,
    PLAY_RINGTONE,
    STOP_RINGTONE,
    COPY,
    SNI_PASTE,
    SHARE,
    RUN_TASK,
    SHELL,
    BREAK_TASK,

    CHECK_EXIST_TEXT,
    CHECK_EXIST_TEXTS,
    CHECK_EXIST_TEXT_OCR,
    OCR_TEXT_STATE,

    STRING_FROM_VALUE,
    STRING_TO_INT,
    STRING_ADD,
    STRING_EQUAL,
    STRING_REGEX,

    CHECK_EXIST_IMAGE,
    CHECK_IMAGE,
    IMAGE_STATE,
    IMAGE_SUB_IMAGE,

    CHECK_EXIST_NODE,
    CHECK_EXIST_NODES,
    NODE_INFO_STATE,
    NODE_IS_VALID,
    NODE_CHILDREN,
    NODE_PARENT,
    NODES_IN_POS,
    NODES_IN_WINDOW,

    CHECK_EXIST_COLOR,
    CHECK_EXIST_COLORS,
    CHECK_COLOR,
    COLOR_STATE,

    BOOL_OR,
    BOOL_AND,
    BOOL_NOT,

    INT_ADD,
    INT_REDUCE,
    INT_MULTI,
    INT_DIV,
    INT_MOD,
    INT_EQUAL,
    INT_LARGE,
    INT_SMALL,
    INT_IN_AREA,
    INT_RANDOM,
    INT_TO_VALUE_AREA,

    POS_FROM_INT,
    POS_TO_INT,
    POS_OFFSET,
    POS_IN_AREA,
    POS_TO_AREA,
    POS_TO_TOUCH,

    AREA_TO_INT,
    AREA_PICK,

    ARRAY_GET,
    ARRAY_SET,
    ARRAY_MAKE,
    ARRAY_ADD,
    ARRAY_INSERT,
    ARRAY_REMOVE,
    ARRAY_CLEAR,
    ARRAY_SIZE,
    ARRAY_VALID_INDEX,
    ARRAY_APPEND,
    ARRAY_INDEX_OF,
    ARRAY_FOR,

    CHECK_IN_APP,
    CHECK_ON_BATTERY_STATE,
    CHECK_ON_SCREEN_STATE,
    CHECK_NETWORK,
    APP_STATE,
    BATTERY_STATE,
    SCREEN_STATE,
    NETWORK_STATE,
    CAPTURE_STATE,
    DATE_STATE,
    TIME_STATE,
    ;

    public ActionConfigInfo getConfig() {
        return switch (this) {
            case CUSTOM -> new ActionConfigInfo(0, 0, FunctionReferenceAction.class);
            case CUSTOM_PIN -> new ActionConfigInfo(0, 0, FunctionPinsAction.class);
            case CUSTOM_START -> new ActionConfigInfo(R.string.function_start, 0, FunctionStartAction.class);
            case CUSTOM_END -> new ActionConfigInfo(R.string.function_end, 0, FunctionEndAction.class);

            case VAR_GET -> new ActionConfigInfo(R.string.action_get_value_action_title, R.drawable.icon_get_value, GetLocalVariableValue.class);
            case VAR_SET -> new ActionConfigInfo(R.string.action_set_value_action_title, R.drawable.icon_set_value, SetLocalVariableValue.class);
            case COMMON_VAR_GET -> new ActionConfigInfo(R.string.action_get_common_value_action_title, R.drawable.icon_get_value, GetCommonVariableValue.class);
            case COMMON_VAR_SET -> new ActionConfigInfo(R.string.action_set_common_value_action_title, R.drawable.icon_set_value, SetCommonVariableValue.class);

            case MANUAL_START -> new ActionConfigInfo(R.string.action_manual_start_title, R.drawable.icon_hand, ManualStartAction.class);
            case ENTER_APP_START -> new ActionConfigInfo(R.string.action_app_start_title, R.drawable.icon_package_info, AppStartAction.class);
            case TIME_START -> new ActionConfigInfo(R.string.action_time_start_title, R.drawable.icon_time, TimeStartAction.class);
            case NOTIFY_START -> new ActionConfigInfo(R.string.action_notification_start_title, R.drawable.icon_notification, NotifyStartAction.class);
            case NETWORK_START -> new ActionConfigInfo(R.string.action_network_start_title, R.drawable.icon_network, NetworkStartAction.class);
            case BATTERY_START -> new ActionConfigInfo(R.string.action_battery_start_title, R.drawable.icon_battery, BatteryStartAction.class);
            case OUTER_START -> new ActionConfigInfo(R.string.action_outer_start_title, R.drawable.icon_auto_start, OuterStartAction.class);
            case NORMAL_START -> new ActionConfigInfo(R.string.action_normal_start_title, 0, NormalStartAction.class);

            case LOGIC_IF -> new ActionConfigInfo(R.string.action_condition_logic_title, R.drawable.icon_condition, IfLogicAction.class);
            case LOGIC_WAIT_IF -> new ActionConfigInfo(R.string.action_wait_condition_logic_title, R.drawable.icon_wait_condition, WaitIfLogicAction.class);
            case LOGIC_FOR -> new ActionConfigInfo(R.string.action_for_loop_logic_title, R.drawable.icon_for_loop, ForLogicAction.class);
            case LOGIC_WHILE -> new ActionConfigInfo(R.string.action_condition_while_logic_title, R.drawable.icon_condition_while, WhileLogicAction.class);
            case LOGIC_SEQUENCE -> new ActionConfigInfo(R.string.action_sequence_logic_title, R.drawable.icon_sequence, SequenceAction.class);
            case LOGIC_RANDOM -> new ActionConfigInfo(R.string.action_random_logic_title, R.drawable.icon_random, RandomAction.class);
            case LOGIC_PARALLEL -> new ActionConfigInfo(R.string.action_parallel_logic_title, R.drawable.icon_parallel, ParallelAction.class);
            case LOGIC_MANUAL_CHOICE -> new ActionConfigInfo(R.string.action_manual_choice_logic_title, R.drawable.icon_condition, ManualChoiceAction.class);

            case DELAY -> new ActionConfigInfo(R.string.action_delay_action_title, R.drawable.icon_delay, DelayAction.class);
            case LOG -> new ActionConfigInfo(R.string.action_log_action_title, R.drawable.icon_log, LogAction.class);
            case CLICK_POSITION -> new ActionConfigInfo(R.string.action_touch_pos_action_title, R.drawable.icon_position, ClickPositionAction.class);
            case CLICK_NODE -> new ActionConfigInfo(R.string.action_touch_node_action_title, R.drawable.icon_widget, ClickNodeAction.class);
            case CLICK_KEY -> new ActionConfigInfo(R.string.action_system_ability_action_title, R.drawable.icon_screen, ClickKeyAction.class);
            case TOUCH -> new ActionConfigInfo(R.string.action_touch_path_action_title, R.drawable.icon_path, TouchAction.class);
            case INPUT -> new ActionConfigInfo(R.string.action_input_node_action_title, R.drawable.icon_input, InputAction.class);
            case SCREEN_SWITCH -> new ActionConfigInfo(R.string.action_screen_action_title, R.drawable.icon_screen, ScreenSwitchAction.class);
            case CAPTURE_SWITCH -> new ActionConfigInfo(R.string.action_open_capture_action_title, R.drawable.icon_capture, CaptureSwitchAction.class);
            case OPEN_APP -> new ActionConfigInfo(R.string.action_open_app_action_title, R.drawable.icon_package_info, OpenAppAction.class);
            case OPEN_URI -> new ActionConfigInfo(R.string.action_open_url_action_title, R.drawable.icon_uri, OpenUriAction.class);
            case PLAY_RINGTONE -> new ActionConfigInfo(R.string.action_play_ringtone_action_title, R.drawable.icon_notification, PlayRingtoneAction.class);
            case STOP_RINGTONE -> new ActionConfigInfo(R.string.action_stop_ringtone_action_title, R.drawable.icon_notification, StopRingtoneAction.class);
            case COPY -> new ActionConfigInfo(R.string.action_copy_action_title, R.drawable.icon_copy, CopyToClipboardAction.class);
            case SNI_PASTE -> new ActionConfigInfo(R.string.action_sni_paste_action_title, R.drawable.icon_home, SniPasteAction.class);
            case SHARE -> new ActionConfigInfo(R.string.action_share_action_title, R.drawable.icon_export, ShareAction.class);
            case RUN_TASK -> new ActionConfigInfo(R.string.action_do_task_action_title, R.drawable.icon_task, RunTaskAction.class);
            case SHELL -> new ActionConfigInfo(R.string.action_shell_action_title, R.drawable.icon_adb, ShellAction.class, true);
            case BREAK_TASK -> new ActionConfigInfo(R.string.action_break_task_action_title, R.drawable.icon_stop, BreakTaskAction.class);

            case CHECK_EXIST_TEXT -> new ActionConfigInfo(R.string.action_exist_text_check_title, R.drawable.icon_text, ExistTextAction.class);
            case CHECK_EXIST_TEXTS -> new ActionConfigInfo(R.string.action_exist_texts_check_title, R.drawable.icon_text, ExistTextsAction.class);
            case CHECK_EXIST_TEXT_OCR -> new ActionConfigInfo(R.string.action_exist_text_ocr_check_title, R.drawable.icon_text, ExistTextOcrAction.class);
            case OCR_TEXT_STATE -> new ActionConfigInfo(R.string.action_ocr_text_state_title, R.drawable.icon_text, OcrTextStateAction.class);

            case STRING_FROM_VALUE -> new ActionConfigInfo(R.string.action_string_from_value_title, R.drawable.icon_text, StringFromValueAction.class);
            case STRING_TO_INT -> new ActionConfigInfo(R.string.action_string_to_int_title, R.drawable.icon_text, StringToIntAction.class);
            case STRING_ADD -> new ActionConfigInfo(R.string.action_string_add_title, R.drawable.icon_text, StringAddAction.class);
            case STRING_EQUAL -> new ActionConfigInfo(R.string.action_string_equal_title, R.drawable.icon_text, StringEqualAction.class);
            case STRING_REGEX -> new ActionConfigInfo(R.string.action_string_regex_title, R.drawable.icon_text, StringRegexAction.class);

            case CHECK_EXIST_IMAGE -> new ActionConfigInfo(R.string.action_exist_image_check_title, R.drawable.icon_image, ExistImageAction.class);
            case CHECK_IMAGE -> new ActionConfigInfo(R.string.action_image_check_title, R.drawable.icon_image, ImageContainAction.class);
            case IMAGE_STATE -> new ActionConfigInfo(R.string.action_image_state_title, R.drawable.icon_image, ImageStateAction.class);
            case IMAGE_SUB_IMAGE -> new ActionConfigInfo(R.string.action_image_sub_image_title, R.drawable.icon_image, SubImageAction.class);

            case CHECK_EXIST_NODE -> new ActionConfigInfo(R.string.action_exist_node_check_title, R.drawable.icon_widget, ExistNodeAction.class);
            case CHECK_EXIST_NODES -> new ActionConfigInfo(R.string.action_exist_nodes_check_title, R.drawable.icon_widget, ExistNodesAction.class);
            case NODE_INFO_STATE -> new ActionConfigInfo(R.string.action_get_node_info_title, R.drawable.icon_widget, GetNodeInfoStateAction.class);
            case NODE_IS_VALID -> new ActionConfigInfo(R.string.action_is_node_valid_title, R.drawable.icon_widget, IsNodeValidAction.class);
            case NODE_CHILDREN -> new ActionConfigInfo(R.string.action_get_node_children_title, R.drawable.icon_widget, GetNodeChildrenAction.class);
            case NODE_PARENT -> new ActionConfigInfo(R.string.action_get_node_parent_title, R.drawable.icon_widget, GetNodeParentAction.class);
            case NODES_IN_POS -> new ActionConfigInfo(R.string.action_get_nodes_in_pos_title, R.drawable.icon_widget, GetNodesInPosAction.class);
            case NODES_IN_WINDOW -> new ActionConfigInfo(R.string.action_get_nodes_in_window_title, R.drawable.icon_widget, GetNodesInWindowAction.class);

            case CHECK_EXIST_COLOR -> new ActionConfigInfo(R.string.action_exist_color_check_title, R.drawable.icon_color, ExistColorAction.class);
            case CHECK_EXIST_COLORS -> new ActionConfigInfo(R.string.action_exist_colors_check_title, R.drawable.icon_color, ExistColorsAction.class);
            case CHECK_COLOR -> new ActionConfigInfo(R.string.action_color_check_title, R.drawable.icon_color, ColorEqualAction.class);
            case COLOR_STATE -> new ActionConfigInfo(R.string.action_color_state_title, R.drawable.icon_color, ColorStateAction.class);

            case BOOL_OR -> new ActionConfigInfo(R.string.action_bool_convert_or_title, R.drawable.icon_condition, BoolOrAction.class);
            case BOOL_AND -> new ActionConfigInfo(R.string.action_bool_convert_and_title, R.drawable.icon_condition, BoolAndAction.class);
            case BOOL_NOT -> new ActionConfigInfo(R.string.action_bool_convert_not_title, R.drawable.icon_condition, BoolNotAction.class);

            case INT_ADD -> new ActionConfigInfo(R.string.action_int_add_title, R.drawable.icon_number, IntAddAction.class);
            case INT_REDUCE -> new ActionConfigInfo(R.string.action_int_reduce_title, R.drawable.icon_number, IntReduceAction.class);
            case INT_MULTI -> new ActionConfigInfo(R.string.action_int_multi_title, R.drawable.icon_number, IntMultiAction.class);
            case INT_DIV -> new ActionConfigInfo(R.string.action_int_div_title, R.drawable.icon_number, IntDivAction.class);
            case INT_MOD -> new ActionConfigInfo(R.string.action_int_mod_title, R.drawable.icon_number, IntModAction.class);
            case INT_EQUAL -> new ActionConfigInfo(R.string.action_int_equal_title, R.drawable.icon_number, IntEqualAction.class);
            case INT_LARGE -> new ActionConfigInfo(R.string.action_int_large_title, R.drawable.icon_number, IntLargeAction.class);
            case INT_SMALL -> new ActionConfigInfo(R.string.action_int_small_title, R.drawable.icon_number, IntSmallAction.class);
            case INT_IN_AREA -> new ActionConfigInfo(R.string.action_int_in_area_title, R.drawable.icon_number, IntInAreaAction.class);
            case INT_RANDOM -> new ActionConfigInfo(R.string.action_int_random_title, R.drawable.icon_number, IntRandomAction.class);
            case INT_TO_VALUE_AREA -> new ActionConfigInfo(R.string.action_int_to_value_area_title, R.drawable.icon_number, IntToValueAreaAction.class);

            case POS_FROM_INT -> new ActionConfigInfo(R.string.action_position_from_int_title, R.drawable.icon_position, PosFromIntAction.class);
            case POS_TO_INT -> new ActionConfigInfo(R.string.action_position_to_int_title, R.drawable.icon_position, PosToIntAction.class);
            case POS_OFFSET -> new ActionConfigInfo(R.string.action_position_offset_title, R.drawable.icon_position, PosOffsetAction.class);
            case POS_IN_AREA -> new ActionConfigInfo(R.string.action_position_in_area_title, R.drawable.icon_position, PosInAreaAction.class);
            case POS_TO_AREA -> new ActionConfigInfo(R.string.action_position_to_area_title, R.drawable.icon_position, PosToAreaAction.class);
            case POS_TO_TOUCH -> new ActionConfigInfo(R.string.action_position_to_touch_title, R.drawable.icon_position, PosToTouchAction.class);

            case AREA_TO_INT -> new ActionConfigInfo(R.string.action_area_to_int_title, R.drawable.icon_position, AreaToIntAction.class);
            case AREA_PICK -> new ActionConfigInfo(R.string.action_area_pick_title, R.drawable.icon_position, AreaPickAction.class);

            case ARRAY_GET -> new ActionConfigInfo(R.string.action_array_get_title, R.drawable.icon_array, ArrayGetAction.class);
            case ARRAY_SET -> new ActionConfigInfo(R.string.action_array_set_title, R.drawable.icon_array, ArraySetAction.class);
            case ARRAY_MAKE -> new ActionConfigInfo(R.string.action_array_make_title, R.drawable.icon_array, ArrayMakeAction.class);
            case ARRAY_ADD -> new ActionConfigInfo(R.string.action_array_add_title, R.drawable.icon_array, ArrayAddAction.class);
            case ARRAY_INSERT -> new ActionConfigInfo(R.string.action_array_insert_title, R.drawable.icon_array, ArrayInsertAction.class);
            case ARRAY_REMOVE -> new ActionConfigInfo(R.string.action_array_remove_title, R.drawable.icon_array, ArrayRemoveAction.class);
            case ARRAY_CLEAR -> new ActionConfigInfo(R.string.action_array_clear_title, R.drawable.icon_array, ArrayClearAction.class);
            case ARRAY_SIZE -> new ActionConfigInfo(R.string.action_array_size_title, R.drawable.icon_array, ArraySizeAction.class);
            case ARRAY_VALID_INDEX -> new ActionConfigInfo(R.string.action_array_valid_index_title, R.drawable.icon_array, ArrayValidIndexAction.class);
            case ARRAY_APPEND -> new ActionConfigInfo(R.string.action_array_append_title, R.drawable.icon_array, ArrayAppendAction.class);
            case ARRAY_INDEX_OF -> new ActionConfigInfo(R.string.action_array_index_of_title, R.drawable.icon_array, ArrayIndexOfAction.class);
            case ARRAY_FOR -> new ActionConfigInfo(R.string.action_array_for_title, R.drawable.icon_array, ArrayForLogicAction.class);

            case CHECK_IN_APP -> new ActionConfigInfo(R.string.action_in_app_check_title, R.drawable.icon_package_info, InAppCheckAction.class);
            case CHECK_ON_BATTERY_STATE -> new ActionConfigInfo(R.string.action_battery_state_check_title, R.drawable.icon_battery, OnBatteryStateAction.class);
            case CHECK_ON_SCREEN_STATE -> new ActionConfigInfo(R.string.action_screen_state_check_title, R.drawable.icon_screen, OnScreenStateAction.class);
            case CHECK_NETWORK -> new ActionConfigInfo(R.string.action_network_check_title, R.drawable.icon_network, NetworkCheckAction.class);
            case APP_STATE -> new ActionConfigInfo(R.string.action_app_state_title, R.drawable.icon_package_info, AppStateAction.class);
            case BATTERY_STATE -> new ActionConfigInfo(R.string.action_battery_state_title, R.drawable.icon_battery, BatteryStateAction.class);
            case SCREEN_STATE -> new ActionConfigInfo(R.string.action_screen_state_title, R.drawable.icon_screen, ScreenStateAction.class);
            case NETWORK_STATE -> new ActionConfigInfo(R.string.action_network_state_title, R.drawable.icon_network, NetworkStateAction.class);
            case CAPTURE_STATE -> new ActionConfigInfo(R.string.action_capture_state_title, R.drawable.icon_capture, CaptureStateAction.class);
            case DATE_STATE -> new ActionConfigInfo(R.string.action_date_state_title, R.drawable.icon_date, DateStateAction.class);
            case TIME_STATE -> new ActionConfigInfo(R.string.action_time_state_title, R.drawable.icon_time, TimeStateAction.class);
            default -> new ActionConfigInfo();
        };
    }
}
