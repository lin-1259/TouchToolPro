package top.bogey.touch_tool_pro.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNodeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMap;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogSelectActionBinding;

@SuppressLint("ViewConstructor")
public class SelectActionDialog extends FrameLayout {
    private final LinkedHashMap<ActionMap, ArrayList<Object>> types;

    public SelectActionDialog(@NonNull Context context, CardLayoutView layoutView, Class<? extends PinObject> pinClass, boolean out) {
        super(context);
        DialogSelectActionBinding binding = DialogSelectActionBinding.inflate(LayoutInflater.from(context), this, true);

        types = new LinkedHashMap<>();

        ArrayList<Object> customFunctions = new ArrayList<>();
        ArrayList<Object> variables = new ArrayList<>();

        LinkedHashMap<String, FunctionPinsAction> actions = SaveRepository.getInstance().getAllFunctionActions();
        actions.forEach((id, action) -> {
            if (matchAction(action, pinClass, out)) {
                customFunctions.add(id);
            }
        });

        HashMap<String, PinValue> allVariables = SaveRepository.getInstance().getAllVariables();
        allVariables.forEach((key, value) -> {
            if (value.getClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(value.getClass())) {
                variables.add(new VariableInfo(key, value, 1, out));
            }
        });

        FunctionContext functionContext = layoutView.getFunctionContext();
        if (functionContext instanceof Function function) {
            functionContext = function.getParent();

            function.getVars().forEach((key, value) -> {
                if (value.getClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(value.getClass())) {
                    variables.add(new VariableInfo(key, value, 3, out));
                }
            });
        }
        if (functionContext instanceof Task task) {
            task.getFunctions().forEach(function -> {
                if (matchAction(function.getAction(), pinClass, out)) customFunctions.add(function);
            });

            task.getVars().forEach((key, value) -> {
                if (value.getClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(value.getClass())) {
                    variables.add(new VariableInfo(key, value, 2, out));
                }
            });
        }

        if (!customFunctions.isEmpty()) types.put(ActionMap.CUSTOM, customFunctions);

        HashMap<ActionType, Action> tmpActions = layoutView.getTmpActions();
        for (ActionMap actionMap : ActionMap.values()) {
            ArrayList<Object> actionTypes = new ArrayList<>();
            for (ActionType actionType : actionMap.getTypes()) {
                Action action = tmpActions.get(actionType);
                if (action == null) continue;
                if (matchAction(action, pinClass, out)) {
                    actionTypes.add(actionType);
                }
            }
            if (actionTypes.isEmpty()) continue;
            types.put(actionMap, actionTypes);
        }

        if (!variables.isEmpty()) types.put(ActionMap.VARIABLE, variables);

        SelectActionTreeAdapter adapter = new SelectActionTreeAdapter(layoutView, new TreeNodeManager(), types);
        binding.activityBox.setAdapter(adapter);
    }

    private boolean matchAction(Action action, Class<? extends PinObject> pinClass, boolean out) {
        for (Pin pin : action.getPins()) {
            if ((pin.getPinClass().isAssignableFrom(pinClass) || pinClass.isAssignableFrom(pin.getPinClass())) && pin.isOut() == out) return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return types.isEmpty();
    }

    static class VariableInfo {
        final String key;
        final PinValue value;
        final int from; // 1 = common, 2 = task, 3 = function
        final boolean out;

        public VariableInfo(String key, PinValue value, int from, boolean out) {
            this.key = key;
            this.value = value;
            this.from = from;
            this.out = out;
        }
    }
}
