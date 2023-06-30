package top.bogey.touch_tool.data.action;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.action.CaptureServiceAction;
import top.bogey.touch_tool.data.action.action.DelayAction;
import top.bogey.touch_tool.data.action.action.DoTaskAction;
import top.bogey.touch_tool.data.action.action.InputNodeAction;
import top.bogey.touch_tool.data.action.action.LogAction;
import top.bogey.touch_tool.data.action.action.OpenAppAction;
import top.bogey.touch_tool.data.action.action.OpenUrlAction;
import top.bogey.touch_tool.data.action.action.ScreenAction;
import top.bogey.touch_tool.data.action.action.SystemAbilityAction;
import top.bogey.touch_tool.data.action.action.TouchNodeAction;
import top.bogey.touch_tool.data.action.action.TouchPathAction;
import top.bogey.touch_tool.data.action.action.TouchPosAction;
import top.bogey.touch_tool.data.action.convert.BoolConvertToAnd;
import top.bogey.touch_tool.data.action.convert.BoolConvertToNot;
import top.bogey.touch_tool.data.action.convert.BoolConvertToOr;
import top.bogey.touch_tool.data.action.convert.IntConvertToPosition;
import top.bogey.touch_tool.data.action.convert.PositionConvertToInt;
import top.bogey.touch_tool.data.action.convert.StringConvertToInt;
import top.bogey.touch_tool.data.action.convert.ValueConvertToString;
import top.bogey.touch_tool.data.action.logic.ConditionLogicAction;
import top.bogey.touch_tool.data.action.logic.ConditionWhileLogicAction;
import top.bogey.touch_tool.data.action.logic.ForLoopLogicAction;
import top.bogey.touch_tool.data.action.logic.ParallelLogicAction;
import top.bogey.touch_tool.data.action.logic.RandomLogicAction;
import top.bogey.touch_tool.data.action.logic.SequenceLogicAction;
import top.bogey.touch_tool.data.action.logic.WaitConditionLogicAction;
import top.bogey.touch_tool.data.action.operator.AreaContainAction;
import top.bogey.touch_tool.data.action.operator.IntAddAction;
import top.bogey.touch_tool.data.action.operator.IntDivAction;
import top.bogey.touch_tool.data.action.operator.IntEqualAction;
import top.bogey.touch_tool.data.action.operator.IntLargeAction;
import top.bogey.touch_tool.data.action.operator.IntModAction;
import top.bogey.touch_tool.data.action.operator.IntMultiAction;
import top.bogey.touch_tool.data.action.operator.IntRandomAction;
import top.bogey.touch_tool.data.action.operator.IntReduceAction;
import top.bogey.touch_tool.data.action.operator.IntSmallAction;
import top.bogey.touch_tool.data.action.operator.PositionOffsetAction;
import top.bogey.touch_tool.data.action.operator.StringAddAction;
import top.bogey.touch_tool.data.action.operator.StringContainAction;
import top.bogey.touch_tool.data.action.operator.StringEqualAction;
import top.bogey.touch_tool.data.action.start.AppStartAction;
import top.bogey.touch_tool.data.action.start.BatteryChargingStartAction;
import top.bogey.touch_tool.data.action.start.BatteryStartAction;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.data.action.start.NormalStartAction;
import top.bogey.touch_tool.data.action.start.NotificationStartAction;
import top.bogey.touch_tool.data.action.start.OutStartAction;
import top.bogey.touch_tool.data.action.start.TimeStartAction;
import top.bogey.touch_tool.data.action.state.AppStateAction;
import top.bogey.touch_tool.data.action.state.BatteryChargingStateAction;
import top.bogey.touch_tool.data.action.state.BatteryStateAction;
import top.bogey.touch_tool.data.action.state.CaptureStateAction;
import top.bogey.touch_tool.data.action.state.ColorStateAction;
import top.bogey.touch_tool.data.action.state.DateStateAction;
import top.bogey.touch_tool.data.action.state.ImageStateAction;
import top.bogey.touch_tool.data.action.state.ScreenStateAction;
import top.bogey.touch_tool.data.action.state.TextStateAction;
import top.bogey.touch_tool.data.action.state.TimeStateAction;
import top.bogey.touch_tool.data.action.state.WidgetStateAction;
import top.bogey.touch_tool.data.action.state.WidgetTextStateAction;
import top.bogey.touch_tool.data.action.state.XPathWidgetStateAction;

public class ActionMap {
    private static ActionMap actionMap;
    private final LinkedHashMap<ActionType, ArrayList<ActionInfo>> actions = new LinkedHashMap<>();

    private ActionMap() {
        ArrayList<ActionInfo> startActions = new ArrayList<>();
        actions.put(ActionType.START, startActions);
        startActions.add(new ActionInfo(ManualStartAction.class, R.string.action_manual_start_title, R.drawable.icon_hand));
        startActions.add(new ActionInfo(AppStartAction.class, R.string.action_app_start_title, R.drawable.icon_package_info));
        startActions.add(new ActionInfo(TimeStartAction.class, R.string.action_time_start_title, R.drawable.icon_time));
        startActions.add(new ActionInfo(BatteryStartAction.class, R.string.action_battery_start_title, R.drawable.icon_battery));
        startActions.add(new ActionInfo(BatteryChargingStartAction.class, R.string.action_battery_charging_start_title, R.drawable.icon_charging));
        startActions.add(new ActionInfo(NotificationStartAction.class, R.string.action_notification_start_title, R.drawable.icon_notification));
        startActions.add(new ActionInfo(OutStartAction.class, R.string.action_out_start_title, R.drawable.icon_auto_start));
        startActions.add(new ActionInfo(NormalStartAction.class, R.string.action_normal_start_title, R.drawable.icon_task));

        ArrayList<ActionInfo> logicActions = new ArrayList<>();
        actions.put(ActionType.LOGIC, logicActions);
        logicActions.add(new ActionInfo(ConditionLogicAction.class, R.string.action_condition_logic_title, R.drawable.icon_condition));
        logicActions.add(new ActionInfo(WaitConditionLogicAction.class, R.string.action_wait_condition_logic_title, R.drawable.icon_wait_condition));
        logicActions.add(new ActionInfo(ConditionWhileLogicAction.class, R.string.action_condition_while_logic_title, R.drawable.icon_condition_while));
        logicActions.add(new ActionInfo(ForLoopLogicAction.class, R.string.action_for_loop_logic_title, R.drawable.icon_for_loop));
        logicActions.add(new ActionInfo(SequenceLogicAction.class, R.string.action_sequence_logic_title, R.drawable.icon_sequence));
        logicActions.add(new ActionInfo(RandomLogicAction.class, R.string.action_random_logic_title, R.drawable.icon_random));
        logicActions.add(new ActionInfo(ParallelLogicAction.class, R.string.action_parallel_logic_title, R.drawable.icon_parallel));

        ArrayList<ActionInfo> stateActions = new ArrayList<>();
        actions.put(ActionType.STATE, stateActions);
        stateActions.add(new ActionInfo(AppStateAction.class, R.string.action_app_state_title, R.drawable.icon_package_info));
        stateActions.add(new ActionInfo(TextStateAction.class, R.string.action_text_state_title, R.drawable.icon_text));
        stateActions.add(new ActionInfo(ImageStateAction.class, R.string.action_image_state_title, R.drawable.icon_image));
        stateActions.add(new ActionInfo(ColorStateAction.class, R.string.action_color_state_title, R.drawable.icon_color));
        stateActions.add(new ActionInfo(WidgetStateAction.class, R.string.action_widget_state_title, R.drawable.icon_widget));
        stateActions.add(new ActionInfo(XPathWidgetStateAction.class, R.string.action_xpath_widget_state_title, R.drawable.icon_path));
        stateActions.add(new ActionInfo(WidgetTextStateAction.class, R.string.action_widget_text_state_title, R.drawable.icon_text));
        stateActions.add(new ActionInfo(BatteryStateAction.class, R.string.action_battery_state_title, R.drawable.icon_battery));
        stateActions.add(new ActionInfo(BatteryChargingStateAction.class, R.string.action_battery_charging_state_title, R.drawable.icon_charging));
        stateActions.add(new ActionInfo(ScreenStateAction.class, R.string.action_screen_state_title, R.drawable.icon_screen));
        stateActions.add(new ActionInfo(CaptureStateAction.class, R.string.action_capture_state_title, R.drawable.icon_capture));

        ArrayList<ActionInfo> normalActions = new ArrayList<>();
        actions.put(ActionType.NORMAL, normalActions);
        normalActions.add(new ActionInfo(DelayAction.class, R.string.action_delay_action_title, R.drawable.icon_delay));
        normalActions.add(new ActionInfo(TouchPosAction.class, R.string.action_touch_pos_action_title, R.drawable.icon_position));
        normalActions.add(new ActionInfo(TouchNodeAction.class, R.string.action_touch_node_action_title, R.drawable.icon_widget));
        normalActions.add(new ActionInfo(TouchPathAction.class, R.string.action_touch_path_action_title, R.drawable.icon_path));
        normalActions.add(new ActionInfo(InputNodeAction.class, R.string.action_input_node_action_title, R.drawable.icon_input));
        normalActions.add(new ActionInfo(SystemAbilityAction.class, R.string.action_system_ability_action_title, R.drawable.icon_screen));
        normalActions.add(new ActionInfo(ScreenAction.class, R.string.action_screen_action_title, R.drawable.icon_screen));
        normalActions.add(new ActionInfo(CaptureServiceAction.class, R.string.action_open_capture_action_title, R.drawable.icon_capture));
        normalActions.add(new ActionInfo(OpenAppAction.class, R.string.action_open_app_action_title, R.drawable.icon_package_info));
        normalActions.add(new ActionInfo(OpenUrlAction.class, R.string.action_open_url_action_title, R.drawable.icon_uri));
        normalActions.add(new ActionInfo(DoTaskAction.class, R.string.action_do_task_action_title, R.drawable.icon_task));
        normalActions.add(new ActionInfo(LogAction.class, R.string.action_log_action_title, R.drawable.icon_log));

        ArrayList<ActionInfo> conditionActions = new ArrayList<>();
        actions.put(ActionType.CONDITION, conditionActions);
        conditionActions.add(new ActionInfo(BoolConvertToOr.class, R.string.action_bool_convert_or_title, 0));
        conditionActions.add(new ActionInfo(BoolConvertToAnd.class, R.string.action_bool_convert_and_title, 0));
        conditionActions.add(new ActionInfo(BoolConvertToNot.class, R.string.action_bool_convert_not_title, 0));

        ArrayList<ActionInfo> stringActions = new ArrayList<>();
        actions.put(ActionType.STRING, stringActions);
        stringActions.add(new ActionInfo(ValueConvertToString.class, R.string.action_value_convert_string_title, 0));
        stringActions.add(new ActionInfo(StringConvertToInt.class, R.string.action_string_convert_int_title, 0));
        stringActions.add(new ActionInfo(StringAddAction.class, R.string.action_string_add_operator_title, 0));
        stringActions.add(new ActionInfo(StringEqualAction.class, R.string.action_string_equal_operator_title, 0));
        stringActions.add(new ActionInfo(StringContainAction.class, R.string.action_string_contain_operator_title, 0));

        ArrayList<ActionInfo> intActions = new ArrayList<>();
        actions.put(ActionType.INT, intActions);
        intActions.add(new ActionInfo(IntAddAction.class, R.string.action_int_add_operator_title, 0));
        intActions.add(new ActionInfo(IntReduceAction.class, R.string.action_int_reduce_operator_title, 0));
        intActions.add(new ActionInfo(IntMultiAction.class, R.string.action_int_multi_operator_title, 0));
        intActions.add(new ActionInfo(IntDivAction.class, R.string.action_int_div_operator_title, 0));
        intActions.add(new ActionInfo(IntModAction.class, R.string.action_int_mod_operator_title, 0));
        intActions.add(new ActionInfo(IntEqualAction.class, R.string.action_int_equal_operator_title, 0));
        intActions.add(new ActionInfo(IntLargeAction.class, R.string.action_int_large_operator_title, 0));
        intActions.add(new ActionInfo(IntSmallAction.class, R.string.action_int_small_operator_title, 0));
        intActions.add(new ActionInfo(IntRandomAction.class, R.string.action_int_random_operator_title, 0));

        ArrayList<ActionInfo> positionActions = new ArrayList<>();
        actions.put(ActionType.POSITION, positionActions);
        positionActions.add(new ActionInfo(IntConvertToPosition.class, R.string.action_int_convert_position_title, 0));
        positionActions.add(new ActionInfo(PositionConvertToInt.class, R.string.action_position_convert_int_title, 0));
        positionActions.add(new ActionInfo(PositionOffsetAction.class, R.string.action_position_add_operator_title, 0));
        positionActions.add(new ActionInfo(AreaContainAction.class, R.string.action_area_contain_operator_title, 0));

        ArrayList<ActionInfo> timeActions = new ArrayList<>();
        actions.put(ActionType.TIME, timeActions);
        timeActions.add(new ActionInfo(DateStateAction.class, R.string.action_date_state_title, 0));
        timeActions.add(new ActionInfo(TimeStateAction.class, R.string.action_time_state_title, 0));
    }

    public static ActionMap getInstance() {
        if (actionMap == null) actionMap = new ActionMap();
        return actionMap;
    }

    public LinkedHashMap<ActionType, ArrayList<ActionInfo>> getActions() {
        return actions;
    }

    public enum ActionType {
        START, LOGIC, STATE, NORMAL, CONDITION, STRING, INT, POSITION, TIME;

        public String getTitle(Context context) {
            String[] strings = context.getResources().getStringArray(R.array.action_type);
            return strings[ordinal()];
        }
    }

    public static class ActionInfo {
        private final Class<? extends BaseAction> cls;

        @StringRes
        private final int title;

        @DrawableRes
        private final int icon;

        public ActionInfo(Class<? extends BaseAction> cls, @StringRes int title, @DrawableRes int icon) {
            this.cls = cls;
            this.title = title;
            this.icon = icon;
        }

        public Class<? extends BaseAction> getCls() {
            return cls;
        }

        public int getTitle() {
            return title;
        }

        public int getIcon() {
            return icon;
        }

    }
}
