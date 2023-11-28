package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.function.FunctionReferenceAction;
import top.bogey.touch_tool_pro.bean.action.normal.RunTaskAction;
import top.bogey.touch_tool_pro.bean.action.var.GetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextBinding;

public class HandleFunctionContextView extends FrameLayout {
    private DialogHandleActionContextBinding binding;
    private HandleTaskAdapter taskAdapter;
    private HandleFunctionAdapter functionAdapter;

    private final HashMap<String, Task> tasks = new HashMap<>();
    private final HashMap<String, Function> functions = new HashMap<>();

    private final HashMap<String, HashSet<String>> taskRequireFunctionIds = new HashMap<>();
    private final HashMap<String, HashSet<String>> taskRequireTaskIds = new HashMap<>();
    private final HashMap<String, HashSet<String>> functionRequireFunctionIds = new HashMap<>();
    private final HashMap<String, HashSet<String>> functionRequireTaskIds = new HashMap<>();

    // 导出
    public HandleFunctionContextView(@NonNull Context context) {
        super(context);
        SaveRepository.getInstance().getAllTasks().forEach(task -> tasks.put(task.getId(), task));
        SaveRepository.getInstance().getAllFunctions().forEach(function -> functions.put(function.getId(), function));

        HashMap<String, Task> taskMap = new HashMap<>();
        HashMap<String, Function> functionMap = new HashMap<>();
        ArrayList<FunctionContext> functionContexts = new ArrayList<>();
        functionContexts.addAll(tasks.values());
        functionContexts.addAll(functions.values());
        searchContext(functionContexts, taskMap, functionMap);
        initUI(taskMap, functionMap);

        switchState(true);
    }

    // 导出
    public HandleFunctionContextView(@NonNull Context context, HashMap<String, FunctionContext> contextMap, Class<? extends FunctionContext> contextClass) {
        super(context);
        SaveRepository.getInstance().getAllTasks().forEach(task -> tasks.put(task.getId(), task));
        SaveRepository.getInstance().getAllFunctions().forEach(function -> functions.put(function.getId(), function));

        // 计算引用
        HashMap<String, Task> taskMap = new HashMap<>();
        HashMap<String, Function> functionMap = new HashMap<>();
        ArrayList<FunctionContext> functionContexts = new ArrayList<>();
        functionContexts.addAll(tasks.values());
        functionContexts.addAll(functions.values());
        searchContext(functionContexts, taskMap, functionMap);

        // 计算必须的
        HashSet<String> requireTaskIds = new HashSet<>();
        HashSet<String> requireFunctionIds = new HashSet<>();
        if (contextClass.equals(Task.class)) {
            searchAllRequireIdsInTasks(contextMap.keySet(), requireTaskIds, requireFunctionIds);
        } else {
            searchAllRequireIdsInFunctions(contextMap.keySet(), requireTaskIds, requireFunctionIds);
        }

        // 提取必须的并初始化
        taskMap.clear();
        functionMap.clear();
        requireTaskIds.forEach(id -> taskMap.put(id, tasks.get(id)));
        requireFunctionIds.forEach(id -> functionMap.put(id, functions.get(id)));
        initUI(taskMap, functionMap);

        switchState(true);
    }

    // 导入
    public HandleFunctionContextView(@NonNull Context context, ArrayList<FunctionContext> functionContexts) {
        super(context);

        functionContexts.forEach(functionContext -> {
            if (functionContext instanceof Task task) {
                tasks.put(task.getId(), task);
            } else if (functionContext instanceof Function function) {
                functions.put(function.getId(), function);
            }
        });

        HashMap<String, Task> taskMap = new HashMap<>();
        HashMap<String, Function> functionMap = new HashMap<>();
        searchContext(functionContexts, taskMap, functionMap);
        initUI(taskMap, functionMap);

        binding.importMoreBox.setVisibility(VISIBLE);
    }

    private void initUI(HashMap<String, Task> tasks, HashMap<String, Function> functions) {
        binding = DialogHandleActionContextBinding.inflate(LayoutInflater.from(getContext()), this, true);
        binding.taskCheckBox.setOnClickListener(v -> taskAdapter.selectAll(binding.taskCheckBox.isChecked()));
        binding.functionCheckBox.setOnClickListener(v -> functionAdapter.selectAll(binding.functionCheckBox.isChecked()));

        taskAdapter = new HandleTaskAdapter(this, tasks);
        binding.tasksBox.setAdapter(taskAdapter);

        functionAdapter = new HandleFunctionAdapter(this, functions);
        binding.functionsBox.setAdapter(functionAdapter);
    }

    private void searchContext(ArrayList<FunctionContext> contexts, HashMap<String, Task> taskMap, HashMap<String, Function> functionMap) {
        contexts.forEach(context -> {
            if (context instanceof Task task) {
                taskMap.put(task.getId(), task);
                searchContextInActions(task, task.getActions(), taskMap, functionMap);
                task.getFunctions().forEach(function -> searchContextInActions(task, function.getActions(), taskMap, functionMap));
            } else if (context instanceof Function function) {
                functionMap.put(function.getId(), function);
                searchContextInActions(function, function.getActions(), taskMap, functionMap);
            }
        });
    }

    // 搜索任务内动作或任务内方法的动作
    private void searchContextInActions(FunctionContext context, HashSet<Action> actions, HashMap<String, Task> taskMap, HashMap<String, Function> functionMap) {
        HashMap<String, HashSet<String>> requireTaskIds = context instanceof Task ? taskRequireTaskIds : functionRequireTaskIds;
        HashMap<String, HashSet<String>> requireFunctionIds = context instanceof Task ? taskRequireFunctionIds : functionRequireFunctionIds;
        HashSet<String> taskIds = requireTaskIds.computeIfAbsent(context.getId(), k -> new HashSet<>());
        HashSet<String> functionIds = requireFunctionIds.computeIfAbsent(context.getId(), k->new HashSet<>());

        actions.forEach(action -> {
            if (action instanceof RunTaskAction runTaskAction) {
                Pin taskPin = runTaskAction.getTaskPin();
                if (taskPin.getLinks().isEmpty()) {
                    PinTask pinTask = taskPin.getValue(PinTask.class);
                    Task runTask = tasks.get(pinTask.getTaskId());
                    // 未采集过的任务
                    if (runTask != null && !taskIds.contains(runTask.getId())) {
                        taskMap.put(runTask.getId(), runTask);
                        requireTaskIds.computeIfAbsent(context.getId(), k -> new HashSet<>()).add(runTask.getId());
                        searchContextInActions(runTask, runTask.getActions(), taskMap, functionMap);
                        runTask.getFunctions().forEach(function -> searchContextInActions(runTask, function.getActions(), taskMap, functionMap));
                    }
                }
            } else if (action instanceof FunctionReferenceAction referenceAction) {
                // 通用方法，需要记录依赖。只判断通用方法，任务内方法有额外判断
                if (referenceAction.getParentId() == null || referenceAction.getParentId().isEmpty()) {
                    Function function = functions.get(referenceAction.getFunctionId());
                    // 未采集过的方法
                    if (function != null && !functionIds.contains(function.getId())) {
                        functionMap.put(function.getId(), function);
                        requireFunctionIds.computeIfAbsent(context.getId(), k -> new HashSet<>()).add(function.getId());
                        searchContextInActions(function, function.getActions(), taskMap, functionMap);
                    }
                }
            }
        });
    }

    public void refreshSelectRequire() {
        HashSet<String> requireTaskIds = new HashSet<>();
        HashSet<String> requireFunctionIds = new HashSet<>();

        HashMap<String, Task> selectedTasks = taskAdapter.getSelectedTasks();
        HashMap<String, Function> selectedFunctions = functionAdapter.getSelectedFunctions();
        searchAllRequireIdsInTasks(selectedTasks.keySet(), requireTaskIds, requireFunctionIds);
        searchAllRequireIdsInFunctions(selectedFunctions.keySet(), requireTaskIds, requireFunctionIds);
        taskAdapter.setRequiredTasks(requireTaskIds);
        functionAdapter.setRequireFunctions(requireFunctionIds);
    }

    public void searchAllRequireIdsInTasks(Set<String> checkTaskIds, HashSet<String> requireTaskIds, HashSet<String> requireFunctionIds) {
        checkTaskIds.forEach(taskId -> {
            // 检查过的任务不再检查
            if (requireTaskIds.contains(taskId)) return;
            // 添加依赖
            requireTaskIds.add(taskId);

            // 检查当前id的依赖
            HashSet<String> taskIds = taskRequireTaskIds.get(taskId);
            if (taskIds != null) {
                searchAllRequireIdsInTasks(taskIds, requireTaskIds, requireFunctionIds);
            }

            HashSet<String> functionIds = taskRequireFunctionIds.get(taskId);
            if (functionIds != null) searchAllRequireIdsInFunctions(functionIds, requireTaskIds, requireFunctionIds);
        });
    }

    public void searchAllRequireIdsInFunctions(Set<String> checkFunctionIds, HashSet<String> requireTaskIds, HashSet<String> requireFunctionIds) {
        checkFunctionIds.forEach(functionId -> {
            // 检查过的方法不再检查
            if (requireFunctionIds.contains(functionId)) return;
            // 添加依赖
            requireFunctionIds.add(functionId);

            // 检查当前方法的依赖
            HashSet<String> ids = functionRequireTaskIds.get(functionId);
            if (ids != null) searchAllRequireIdsInTasks(ids, requireTaskIds, requireFunctionIds);

            ids = functionRequireFunctionIds.get(functionId);
            if (ids != null) searchAllRequireIdsInFunctions(ids, requireTaskIds, requireFunctionIds);
        });
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

    public void importSelectFunctionContext() {
        boolean importTag = binding.importTag.isChecked();
        boolean importCommonAttr = binding.importCommonAttr.isChecked();

        HashMap<String, Task> selectedTasks = taskAdapter.getAllSelectedTasks();
        HashMap<String, Function> selectedFunctions = functionAdapter.getAllSelectedFunctions();
        selectedTasks.forEach((id, task) -> {
            HashSet<String> tags = task.getTags();
            if (tags != null) {
                if (importTag) {
                    tags.forEach(tag -> {
                        if (tag.equals(SaveRepository.SHORTCUT_TAG)) return;
                        SaveRepository.getInstance().addTaskTag(tag);
                    });
                } else {
                    ArrayList<String> taskTags = SaveRepository.getInstance().getTaskTags();
                    HashSet<String> set = new HashSet<>(tags);
                    set.forEach(tag -> {
                        if (!taskTags.contains(tag)) task.removeTag(tag);
                    });
                }
            }
            task.save();
        });

        selectedFunctions.forEach((id, function) -> {
            HashSet<String> tags = function.getTags();
            if (tags != null) {
                if (importTag) {
                    tags.forEach(tag -> SaveRepository.getInstance().addFunctionTag(tag));
                } else {
                    ArrayList<String> functionTags = SaveRepository.getInstance().getFunctionTags();
                    HashSet<String> set = new HashSet<>(tags);
                    set.forEach(tag -> {
                        if (!functionTags.contains(tag)) function.removeTag(tag);
                    });
                }
            }
            function.save();
        });

        if (importCommonAttr) {
            HashMap<String, PinValue> commonVar = new HashMap<>();
            searchCommonVar(new HashSet<>(selectedTasks.values()), commonVar);
            searchCommonVar(new HashSet<>(selectedFunctions.values()), commonVar);
            if (!commonVar.isEmpty()) {
                commonVar.forEach((key, value) -> SaveRepository.getInstance().addVariable(key, value));
            }
        }
    }

    private void searchCommonVar(Set<FunctionContext> contexts, HashMap<String, PinValue> commonVar) {
        contexts.forEach(context -> {
            context.getActions().forEach(action -> {
                if (action instanceof GetCommonVariableValue getValue) {
                    commonVar.put(getValue.getVarKey(), getValue.getValue());
                } else if (action instanceof SetCommonVariableValue setValue) {
                    commonVar.put(setValue.getVarKey(), setValue.getValue());
                }
            });

            if (context instanceof Task task) {
                searchCommonVar(new HashSet<>(task.getFunctions()), commonVar);
            }
        });
    }

    public ArrayList<FunctionContext> getSelectFunctionContext() {
        ArrayList<FunctionContext> list = new ArrayList<>();
        HashMap<String, Task> selectedTasks = taskAdapter.getAllSelectedTasks();
        HashMap<String, Function> selectedFunctions = functionAdapter.getAllSelectedFunctions();
        list.addAll(selectedTasks.values());
        list.addAll(selectedFunctions.values());
        return list;
    }

    public boolean isEmpty() {
        return taskAdapter.getItemCount() + functionAdapter.getItemCount() == 0;
    }
}
