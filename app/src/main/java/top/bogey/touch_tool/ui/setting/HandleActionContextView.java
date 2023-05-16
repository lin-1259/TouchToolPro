package top.bogey.touch_tool.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.DialogHandleActionContextBinding;

public class HandleActionContextView extends FrameLayout {
    private final DialogHandleActionContextBinding binding;
    private final TaskRepository repository = TaskRepository.getInstance();
    private ActionContextAdapter taskAdapter;
    private ActionContextAdapter functionAdapter;

    public HandleActionContextView(@NonNull Context context) {
        super(context);
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(context), this, true);

        init(new ArrayList<>(repository.getAllTasks()), null, new ArrayList<>(repository.getFunctions()), null);
    }

    public HandleActionContextView(@NonNull Context context, ArrayList<ActionContext> actionContexts) {
        super(context);
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(context), this, true);

        ArrayList<ActionContext> tmpTasks = new ArrayList<>();
        ArrayList<ActionContext> repeatTasks = new ArrayList<>();

        HashMap<String, ActionContext> tmpFunctions = new HashMap<>();
        HashMap<String, ActionContext> repeatFunctions = new HashMap<>();

        for (ActionContext actionContext : actionContexts) {
            searchAllActionContext(tmpTasks, repeatTasks, tmpFunctions, repeatFunctions, actionContext);
        }

        init(tmpTasks, repeatTasks, new ArrayList<>(tmpFunctions.values()), new ArrayList<>(repeatFunctions.values()));
    }

    private void searchAllActionContext(ArrayList<ActionContext> tasks, ArrayList<ActionContext> repeatTasks, HashMap<String, ActionContext> functions, HashMap<String, ActionContext> repeatFunctions, ActionContext actionContext) {
        if (actionContext instanceof Task) {
            Task task = (Task) actionContext;
            task.setTag(null);
            if (repository.getTaskById(task.getId()) == null) {
                tasks.add(task);
            } else {
                repeatTasks.add(task);
            }

            for (BaseFunction function : task.getFunctions()) {
                searchAllActionContext(tasks, repeatTasks, functions, repeatFunctions, function);
            }

        } else if (actionContext instanceof BaseFunction) {
            BaseFunction function = (BaseFunction) actionContext;
            // 只导入通用卡
            if (function.getTaskId() == null || function.getTaskId().isEmpty()) {
                if (repository.getFunctionById(function.getFunctionId()) == null) {
                    functions.put(function.getFunctionId(), function);
                } else {
                    repeatFunctions.put(function.getFunctionId(), function);
                }
            }
        }

        for (BaseAction action : actionContext.getActions()) {
            if (action instanceof ActionContext) {
                searchAllActionContext(tasks, repeatTasks, functions, repeatFunctions, (ActionContext) action);
            }
        }
    }

    private void init(ArrayList<ActionContext> tasks, ArrayList<ActionContext> repeatTasks, ArrayList<ActionContext> functions, ArrayList<ActionContext> repeatFunctions) {
        taskAdapter = new ActionContextAdapter(tasks, repeatTasks);
        binding.tasksBox.setAdapter(taskAdapter);

        functionAdapter = new ActionContextAdapter(functions, repeatFunctions);
        binding.functionsBox.setAdapter(functionAdapter);
    }

    public ArrayList<ActionContext> getSelectActionContext() {
        ArrayList<ActionContext> list = new ArrayList<>();
        list.addAll(taskAdapter.getSelectedContexts());
        list.addAll(functionAdapter.getSelectedContexts());
        return list;
    }

    public ArrayList<ActionContext> getShowActionContext() {
        ArrayList<ActionContext> list = new ArrayList<>();
        list.addAll(taskAdapter.getActionContexts());
        list.addAll(functionAdapter.getActionContexts());
        return list;
    }
}
