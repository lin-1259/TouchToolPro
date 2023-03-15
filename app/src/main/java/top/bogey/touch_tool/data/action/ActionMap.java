package top.bogey.touch_tool.data.action;

import android.content.Context;

import java.util.LinkedHashMap;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.action.CaptureServiceAction;
import top.bogey.touch_tool.data.action.action.DelayAction;
import top.bogey.touch_tool.data.action.action.InputNodeAction;
import top.bogey.touch_tool.data.action.action.LogAction;
import top.bogey.touch_tool.data.action.action.OpenAppAction;
import top.bogey.touch_tool.data.action.action.OpenUrlAction;
import top.bogey.touch_tool.data.action.action.ScreenAction;
import top.bogey.touch_tool.data.action.action.SystemAbilityAction;
import top.bogey.touch_tool.data.action.action.TouchPathAction;
import top.bogey.touch_tool.data.action.action.TouchPosAction;
import top.bogey.touch_tool.data.action.action.TouchNodeAction;
import top.bogey.touch_tool.data.action.convert.BoolConvertToAnd;
import top.bogey.touch_tool.data.action.convert.BoolConvertToNot;
import top.bogey.touch_tool.data.action.convert.BoolConvertToOr;
import top.bogey.touch_tool.data.action.convert.IntConvertToPosition;
import top.bogey.touch_tool.data.action.convert.PositionConvertToInt;
import top.bogey.touch_tool.data.action.convert.ValueConvertToString;
import top.bogey.touch_tool.data.action.logic.ConditionLogicAction;
import top.bogey.touch_tool.data.action.logic.ConditionWhileLogicAction;
import top.bogey.touch_tool.data.action.logic.ForLoopLogicAction;
import top.bogey.touch_tool.data.action.logic.ParallelLogicAction;
import top.bogey.touch_tool.data.action.logic.RandomLogicAction;
import top.bogey.touch_tool.data.action.logic.SequenceLogicAction;
import top.bogey.touch_tool.data.action.logic.WaitConditionLogicAction;
import top.bogey.touch_tool.data.action.operator.IntAddAction;
import top.bogey.touch_tool.data.action.operator.IntDivAction;
import top.bogey.touch_tool.data.action.operator.IntEqualAction;
import top.bogey.touch_tool.data.action.operator.IntLargeAction;
import top.bogey.touch_tool.data.action.operator.IntMultiAction;
import top.bogey.touch_tool.data.action.operator.IntRandomAction;
import top.bogey.touch_tool.data.action.operator.IntReduceAction;
import top.bogey.touch_tool.data.action.operator.IntSmallAction;
import top.bogey.touch_tool.data.action.operator.StringAddAction;
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
import top.bogey.touch_tool.data.action.state.ImageStateAction;
import top.bogey.touch_tool.data.action.state.ScreenStateAction;
import top.bogey.touch_tool.data.action.state.TextStateAction;
import top.bogey.touch_tool.data.action.state.WidgetStateAction;

public class ActionMap {
    private static ActionMap actionMap;
    private final LinkedHashMap<ActionType, LinkedHashMap<Class<? extends BaseAction>, Integer>> actions = new LinkedHashMap<>();

    private ActionMap() {
        LinkedHashMap<Class<? extends BaseAction>, Integer> startActions = new LinkedHashMap<>();
        actions.put(ActionType.START, startActions);
        startActions.put(NormalStartAction.class, R.string.action_normal_start_title);
        startActions.put(ManualStartAction.class, R.string.action_manual_start_title);
        startActions.put(AppStartAction.class, R.string.action_app_start_title);
        startActions.put(TimeStartAction.class, R.string.action_time_start_title);
        startActions.put(BatteryStartAction.class, R.string.action_battery_start_title);
        startActions.put(BatteryChargingStartAction.class, R.string.action_battery_charging_start_title);
        startActions.put(NotificationStartAction.class, R.string.action_notification_start_title);
        startActions.put(OutStartAction.class, R.string.action_out_start_title);

        LinkedHashMap<Class<? extends BaseAction>, Integer> logicActions = new LinkedHashMap<>();
        actions.put(ActionType.LOGIC, logicActions);
        logicActions.put(ConditionLogicAction.class, R.string.action_condition_logic_title);
        logicActions.put(WaitConditionLogicAction.class, R.string.action_wait_condition_logic_title);
        logicActions.put(ConditionWhileLogicAction.class, R.string.action_condition_while_logic_title);
        logicActions.put(ForLoopLogicAction.class, R.string.action_for_loop_logic_title);
        logicActions.put(SequenceLogicAction.class, R.string.action_sequence_logic_title);
        logicActions.put(RandomLogicAction.class, R.string.action_random_logic_title);
        logicActions.put(ParallelLogicAction.class, R.string.action_parallel_logic_title);

        LinkedHashMap<Class<? extends BaseAction>, Integer> normalActions = new LinkedHashMap<>();
        actions.put(ActionType.NORMAL, normalActions);
        normalActions.put(DelayAction.class, R.string.action_delay_action_title);
        normalActions.put(TouchPosAction.class, R.string.action_touch_pos_action_title);
        normalActions.put(TouchNodeAction.class, R.string.action_touch_node_action_title);
        normalActions.put(TouchPathAction.class, R.string.action_touch_path_action_title);
        normalActions.put(InputNodeAction.class, R.string.action_input_node_action_title);
        normalActions.put(SystemAbilityAction.class, R.string.action_system_ability_action_title);
        normalActions.put(ScreenAction.class, R.string.action_screen_action_title);
        normalActions.put(CaptureServiceAction.class, R.string.action_open_capture_action_title);
        normalActions.put(OpenAppAction.class, R.string.action_open_app_action_title);
        normalActions.put(OpenUrlAction.class, R.string.action_open_url_action_title);
        normalActions.put(LogAction.class, R.string.action_log_action_title);

        LinkedHashMap<Class<? extends BaseAction>, Integer> stateActions = new LinkedHashMap<>();
        actions.put(ActionType.STATE, stateActions);
        stateActions.put(AppStateAction.class, R.string.action_app_state_title);
        stateActions.put(TextStateAction.class, R.string.action_text_state_title);
        stateActions.put(ImageStateAction.class, R.string.action_image_state_title);
        stateActions.put(ColorStateAction.class, R.string.action_color_state_title);
        stateActions.put(WidgetStateAction.class, R.string.action_widget_state_title);
        stateActions.put(BatteryStateAction.class, R.string.action_battery_state_title);
        stateActions.put(BatteryChargingStateAction.class, R.string.action_battery_charging_state_title);
        stateActions.put(ScreenStateAction.class, R.string.action_screen_state_title);
        stateActions.put(CaptureStateAction.class, R.string.action_capture_state_title);

        LinkedHashMap<Class<? extends BaseAction>, Integer> convertActions = new LinkedHashMap<>();
        actions.put(ActionType.CONVERT, convertActions);
        convertActions.put(ValueConvertToString.class, R.string.action_value_convert_string_title);
        convertActions.put(PositionConvertToInt.class, R.string.action_position_convert_int_title);
        convertActions.put(IntConvertToPosition.class, R.string.action_int_convert_position_title);
        convertActions.put(BoolConvertToOr.class, R.string.action_bool_convert_or_title);
        convertActions.put(BoolConvertToAnd.class, R.string.action_bool_convert_and_title);
        convertActions.put(BoolConvertToNot.class, R.string.action_bool_convert_not_title);

        LinkedHashMap<Class<? extends BaseAction>, Integer> operateActions = new LinkedHashMap<>();
        actions.put(ActionType.OPERATE, operateActions);
        operateActions.put(IntAddAction.class, R.string.action_int_add_operator_title);
        operateActions.put(IntReduceAction.class, R.string.action_int_reduce_operator_title);
        operateActions.put(IntMultiAction.class, R.string.action_int_multi_operator_title);
        operateActions.put(IntDivAction.class, R.string.action_int_div_operator_title);
        operateActions.put(IntEqualAction.class, R.string.action_int_equal_operator_title);
        operateActions.put(IntLargeAction.class, R.string.action_int_large_operator_title);
        operateActions.put(IntSmallAction.class, R.string.action_int_small_operator_title);
        operateActions.put(IntRandomAction.class, R.string.action_int_random_operator_title);
        operateActions.put(StringAddAction.class, R.string.action_string_add_operator_title);
    }

    public static ActionMap getInstance() {
        if (actionMap == null) actionMap = new ActionMap();
        return actionMap;
    }

    public LinkedHashMap<ActionType, LinkedHashMap<Class<? extends BaseAction>, Integer>> getActions() {
        return actions;
    }

    public enum ActionType {
        START, LOGIC, NORMAL, STATE, CONVERT, OPERATE, CUSTOM;

        public String getTitle(Context context) {
            String[] strings = context.getResources().getStringArray(R.array.action_type);
            return strings[ordinal()];
        }
    }
}
