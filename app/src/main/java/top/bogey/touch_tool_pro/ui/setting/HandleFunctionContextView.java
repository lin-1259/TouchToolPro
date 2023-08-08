package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextBinding;

public class HandleFunctionContextView extends FrameLayout {
    private final DialogHandleActionContextBinding binding;
    private final SaveRepository repository = SaveRepository.getInstance();
    private FunctionContextAdapter taskAdapter;
    private FunctionContextAdapter functionAdapter;

    public HandleFunctionContextView(@NonNull Context context) {
        super(context);
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(context), this, true);

        init(new ArrayList<>(repository.getAllTasks()), null, new ArrayList<>(repository.getAllFunctions()), null);
    }

    public HandleFunctionContextView(@NonNull Context context, ArrayList<FunctionContext> actionContexts) {
        super(context);
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(context), this, true);

        HashMap<String, Task> tmpTasks = new HashMap<>();
        HashMap<String, Task> repeatTasks = new HashMap<>();

        HashMap<String, Function> tmpFunctions = new HashMap<>();
        HashMap<String, Function> repeatFunctions = new HashMap<>();

        for (FunctionContext actionContext : actionContexts) {
            actionContext.removeAllTags();
            if (actionContext instanceof Task task) {
                if (repository.getTaskById(task.getId()) == null) {
                    tmpTasks.put(task.getId(), task);
                } else {
                    repeatTasks.put(task.getId(), task);
                }
            } else if (actionContext instanceof Function function) {
                // 只导入通用卡
                if (function.getParentId() == null || function.getParentId().isEmpty()) {
                    if (repository.getFunctionById(function.getId()) == null) {
                        tmpFunctions.put(function.getId(), function);
                    } else {
                        repeatFunctions.put(function.getId(), function);
                    }
                }
            }
        }

        init(new ArrayList<>(tmpTasks.values()), new ArrayList<>(repeatTasks.values()), new ArrayList<>(tmpFunctions.values()), new ArrayList<>(repeatFunctions.values()));
    }

    private void init(ArrayList<FunctionContext> tasks, ArrayList<FunctionContext> repeatTasks, ArrayList<FunctionContext> functions, ArrayList<FunctionContext> repeatFunctions) {
        taskAdapter = new FunctionContextAdapter(tasks, repeatTasks);
        binding.tasksBox.setAdapter(taskAdapter);

        functionAdapter = new FunctionContextAdapter(functions, repeatFunctions);
        binding.functionsBox.setAdapter(functionAdapter);
    }

    public ArrayList<FunctionContext> getSelectActionContext() {
        ArrayList<FunctionContext> list = new ArrayList<>();
        list.addAll(taskAdapter.getSelectedContexts());
        list.addAll(functionAdapter.getSelectedContexts());
        return list;
    }

    public ArrayList<FunctionContext> getShowActionContext() {
        ArrayList<FunctionContext> list = new ArrayList<>();
        list.addAll(taskAdapter.getFunctionContexts());
        list.addAll(functionAdapter.getFunctionContexts());
        return list;
    }
}
