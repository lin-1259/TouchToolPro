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

        init(repository.getAllTasks(), repository.getFunctions());
    }

    public HandleActionContextView(@NonNull Context context, ArrayList<ActionContext> actionContexts) {
        super(context);
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(context), this, true);

        ArrayList<Task> tmpTasks = new ArrayList<>();
        HashMap<String, BaseFunction> tmpFunctions = new HashMap<>();
        for (ActionContext actionContext : actionContexts) {
            if (actionContext instanceof Task) {
                Task task = (Task) actionContext;
                if (repository.getTaskById(task.getId()) == null) {
                    tmpTasks.add(task);
                    for (BaseAction action : task.getActions()) {
                        if (action instanceof BaseFunction) {
                            BaseFunction function = (BaseFunction) action;
                            // 通用自定义方法
                            if (function.getTaskId() == null) {
                                if (repository.getFunctionById(function.getFunctionId()) == null) {
                                    tmpFunctions.put(function.getFunctionId(), function);
                                }
                            }
                        }
                    }
                }
            } else if (actionContext instanceof BaseFunction) {
                BaseFunction function = (BaseFunction) actionContext;
                if (repository.getFunctionById(function.getFunctionId()) == null) {
                    tmpFunctions.put(function.getFunctionId(), function);
                }
            }
        }

        init(tmpTasks, new ArrayList<>(tmpFunctions.values()));
    }

    private void init(ArrayList<Task> tasks, ArrayList<BaseFunction> functions) {
        taskAdapter = new ActionContextAdapter(new ArrayList<>(tasks));
        binding.tasksBox.setAdapter(taskAdapter);

        functionAdapter = new ActionContextAdapter(new ArrayList<>(functions));
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
