package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.normal.RunTaskAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextBinding;

public class HandleFunctionContextView extends FrameLayout {
    private final DialogHandleActionContextBinding binding;
    private final HandleTaskAdapter taskAdapter;
    private final HandleFunctionAdapter functionAdapter;

    public HandleFunctionContextView(@NonNull Context context) {
        this(context, SaveRepository.getInstance().getAllTasks(), SaveRepository.getInstance().getAllFunctions());
        switchState(true);
    }

    private static void searchTask(LinkedHashMap<String, Task> tasks, HashSet<FunctionContext> contexts) {
        contexts.forEach(context -> {
            HashSet<FunctionContext> newContexts = new HashSet<>();

            for (Action action : context.getActionsByClass(RunTaskAction.class)) {
                RunTaskAction taskAction = (RunTaskAction) action;
                Pin taskPin = taskAction.getTaskPin();
                if (taskPin.getLinks().isEmpty()) {
                    Task task = taskPin.getValue(PinTask.class).getTask();
                    if (task != null && tasks.get(task.getId()) == null) {
                        tasks.put(task.getId(), task);
                        newContexts.add(task);
                    }
                }
            }

            if (context instanceof Task task) {
                newContexts.addAll(task.getFunctions());
            }
            searchTask(tasks, newContexts);
        });
    }

    private static ArrayList<Task> getTasks(ArrayList<FunctionContext> contexts) {
        LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();
        for (FunctionContext context : contexts) {
            if (context instanceof Task task) {
                tasks.put(task.getId(), task);
            }
        }
        searchTask(tasks, new HashSet<>(tasks.values()));
        return new ArrayList<>(tasks.values());
    }

    private static void searchFunction(LinkedHashMap<String, Function> functions, HashSet<FunctionContext> contexts) {
        contexts.forEach(context -> {
            HashSet<FunctionContext> newContexts = new HashSet<>();

            for (Action action : context.getActionsByClass(FunctionReferenceAction.class)) {
                FunctionReferenceAction referenceAction = (FunctionReferenceAction) action;
                if (referenceAction.getParentId() == null || referenceAction.getParentId().isEmpty()) {
                    Function function = SaveRepository.getInstance().getFunctionById(referenceAction.getFunctionId());
                    if (function != null && functions.get(function.getId()) == null) {
                        functions.put(function.getId(), function);
                        newContexts.add(function);
                    }
                }
            }

            for (Action action : context.getActionsByClass(RunTaskAction.class)) {
                RunTaskAction taskAction = (RunTaskAction) action;
                Pin taskPin = taskAction.getTaskPin();
                if (taskPin.getLinks().isEmpty()) {
                    Task task = taskPin.getValue(PinTask.class).getTask();
                    if (task != null) {
                        newContexts.add(task);
                    }
                }
            }

            if (context instanceof Task task) {
                newContexts.addAll(task.getFunctions());
            }
            searchFunction(functions, newContexts);
        });
    }

    private static ArrayList<Function> getFunctions(ArrayList<FunctionContext> contexts) {
        LinkedHashMap<String, Function> functions = new LinkedHashMap<>();
        for (FunctionContext context : contexts) {
            if (context instanceof Function function) {
                functions.put(function.getId(), function);
            }
        }
        searchFunction(functions, new HashSet<>(contexts));
        return new ArrayList<>(functions.values());
    }

    // 导入
    public HandleFunctionContextView(@NonNull Context context, ArrayList<FunctionContext> actionContexts) {
        this(context, getTasks(actionContexts), getFunctions(actionContexts));
    }

    public HandleFunctionContextView(@NonNull Context context, ArrayList<Task> tasks, ArrayList<Function> functions) {
        super(context);
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(context), this, true);

        ArrayList<String> taskTags = SaveRepository.getInstance().getTaskTags();
        tasks.forEach(task -> {
            if (task.getTags() == null) return;
            HashSet<String> set = new HashSet<>(task.getTags());
            set.forEach(tag -> {
                if (!taskTags.contains(tag)) task.removeTag(tag);
            });
        });
        taskAdapter = new HandleTaskAdapter(this, new ArrayList<>(tasks));
        binding.tasksBox.setAdapter(taskAdapter);

        ArrayList<String> functionTags = SaveRepository.getInstance().getFunctionTags();
        functions.forEach(function -> {
            if (function.getTags() == null) return;
            HashSet<String> set = new HashSet<>(function.getTags());
            set.forEach(tag -> {
                if (!functionTags.contains(tag)) function.removeTag(tag);
            });
        });
        functionAdapter = new HandleFunctionAdapter(this, new ArrayList<>(functions));
        binding.functionsBox.setAdapter(functionAdapter);

        binding.taskCheckBox.setOnClickListener(v -> taskAdapter.selectAll(binding.taskCheckBox.isChecked()));
        binding.functionCheckBox.setOnClickListener(v -> functionAdapter.selectAll(binding.functionCheckBox.isChecked()));
    }

    public void switchState(boolean export) {
        if (export) {
            taskAdapter.selectAll(true);
            functionAdapter.selectAll(true);
        } else {
            taskAdapter.selectNotExist();
            functionAdapter.selectNotExist();
        }
    }

    public void setTaskCheck(int state) {
        if (state == -1) {
            binding.taskCheckBox.setEnabled(false);
        } else {
            binding.taskCheckBox.setEnabled(true);
            binding.taskCheckBox.setCheckedState(state);
        }
    }

    public void setFunctionCheck(int state) {
        if (state == -1) {
            binding.functionCheckBox.setEnabled(false);
        } else {
            binding.functionCheckBox.setEnabled(true);
            binding.functionCheckBox.setCheckedState(state);
        }
    }

    public ArrayList<FunctionContext> getSelectActionContext() {
        ArrayList<FunctionContext> list = new ArrayList<>();
        list.addAll(taskAdapter.getSelectedTasks());
        list.addAll(functionAdapter.getSelectedFunctions());
        return list;
    }

    public boolean isEmpty() {
        return taskAdapter.getItemCount() + functionAdapter.getItemCount() == 0;
    }
}
