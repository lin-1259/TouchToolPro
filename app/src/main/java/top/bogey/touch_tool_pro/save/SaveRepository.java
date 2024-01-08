package top.bogey.touch_tool_pro.save;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.function.FunctionPinsAction;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.base.LogInfo;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.ui.MainActivity;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class SaveRepository {
    public final static String SHORTCUT_TAG = MainApplication.getInstance().getString(R.string.tag_shortcut);
    public final static String NO_TAG = MainApplication.getInstance().getString(R.string.tag_no);
    private final static MMKV taskMMKV = MMKV.mmkvWithID("TASK_DB", MMKV.SINGLE_PROCESS_MODE);
    private final static MMKV functionMMKV = MMKV.mmkvWithID("FUNCTION_DB", MMKV.SINGLE_PROCESS_MODE);
    private final static MMKV variableMMKV = MMKV.mmkvWithID("VARIABLE_DB", MMKV.SINGLE_PROCESS_MODE);
    private final static MMKV loggerMMKV = MMKV.mmkvWithID("LOG_DB", MMKV.MULTI_PROCESS_MODE);
    private final static MMKV taskTagsMMKV = MMKV.mmkvWithID("TASK_TAGS", MMKV.SINGLE_PROCESS_MODE);
    private final static MMKV functionTagsMMKV = MMKV.mmkvWithID("FUNCTION_TAGS", MMKV.SINGLE_PROCESS_MODE);
    private static SaveRepository repository;
    private final Handler handler;
    private final LinkedHashMap<String, TaskSaveReference> tasks = new LinkedHashMap<>();
    private final HashSet<TaskSaveChangedListener> taskListeners = new HashSet<>();

    private final LinkedHashMap<String, FunctionSaveReference> functions = new LinkedHashMap<>();
    private final HashSet<FunctionSaveChangedListener> functionListeners = new HashSet<>();

    private final LinkedHashMap<String, VariableSaveReference> variables = new LinkedHashMap<>();
    private final HashSet<VariableSaveChangedListener> variableListeners = new HashSet<>();

    private SaveRepository() {
        handler = new Handler(Looper.getMainLooper());
        check();
        readAllTasks();
        readAllFunctions();
        readAllVariables();
    }

    public static SaveRepository getInstance() {
        if (repository == null) {
            repository = new SaveRepository();
        }
        return repository;
    }

    private void check() {
        tasks.forEach((taskId, taskReference) -> taskReference.check());
        functions.forEach((functionId, functionReference) -> functionReference.check());
        variables.forEach((variableKey, variableReference) -> variableReference.check());
        handler.postDelayed(this::check, 5 * 60 * 1000);
    }

    //----------------------------------------------- 任务 ------------------------------------------------

    private void readAllTasks() {
        String[] keys = taskMMKV.allKeys();
        if (keys == null) return;

        ArrayList<String> removeKeys = new ArrayList<>();
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
            TaskSaveReference task = new TaskSaveReference(taskMMKV, key);
            if (task.get() == null) {
                removeKeys.add(key);
                continue;
            }
            tasks.put(key, task);
        }

//        removeKeys.forEach(taskMMKV::remove);
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Map.Entry<String, TaskSaveReference> entry : this.tasks.entrySet()) {
            tasks.add(entry.getValue().get());
        }
        return tasks;
    }

    public ArrayList<String> getAllTaskTags() {
        HashSet<String> tags = new HashSet<>();
        boolean existNoTag = false;
        for (Map.Entry<String, TaskSaveReference> entry : tasks.entrySet()) {
            if (entry.getValue().getTags().isEmpty()) existNoTag = true;
            else tags.addAll(entry.getValue().getTags());
        }
        ArrayList<String> list = new ArrayList<>(tags);
        if (existNoTag || tasks.isEmpty()) list.add(NO_TAG);
        return list;
    }

    public LinkedHashMap<String, String> getAllTasksTitle() {
        LinkedHashMap<String, String> titleMap = new LinkedHashMap<>();
        tasks.forEach((taskId, taskRef) -> titleMap.put(taskId, taskRef.getTaskName()));
        return titleMap;
    }

    public Task getTaskById(String id) {
        TaskSaveReference reference = tasks.get(id);
        if (reference != null) return reference.get();
        return null;
    }

    public Task getOriginTaskById(String id) {
        TaskSaveReference reference = tasks.get(id);
        if (reference != null) return reference.getOrigin();
        return null;
    }

    public ArrayList<Task> getTasksByTag(String tag) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Map.Entry<String, TaskSaveReference> entry : this.tasks.entrySet()) {
            if (entry.getValue().existTag(tag)) {
                tasks.add(entry.getValue().get());
            }
        }
        return tasks;
    }

    public ArrayList<Task> getTasksByStart(Class<? extends StartAction> actionClass) {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Map.Entry<String, TaskSaveReference> entry : this.tasks.entrySet()) {
            if (entry.getValue().existClass(actionClass)) {
                tasks.add(entry.getValue().get());
            }
        }
        return tasks;
    }

    public void saveTask(Task task) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) {
            service.replaceAlarm(task);
        }

        TaskSaveReference reference = tasks.get(task.getId());
        if (reference == null) {
            tasks.put(task.getId(), new TaskSaveReference(taskMMKV, task));
            taskListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onCreated(task));
        } else {
            reference.set(task);
            taskListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onChanged(task));
        }

        MainActivity mainActivity = MainApplication.getInstance().getMainActivity();
        if (mainActivity != null) mainActivity.sendShortcuts();
    }

    public void removeTask(String id) {
        TaskSaveReference reference = tasks.remove(id);
        if (reference != null) {
            Task task = reference.get();
            taskListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onRemoved(task));
            reference.remove();
            removeLog(task.getId());

            if (task.getTags() != null && task.getTags().contains(SHORTCUT_TAG)) {
                MainActivity mainActivity = MainApplication.getInstance().getMainActivity();
                if (mainActivity != null) mainActivity.sendShortcuts();
            }
        }
    }

    public void addTaskListener(TaskSaveChangedListener listener) {
        taskListeners.add(listener);
    }

    public void removeTaskListener(TaskSaveChangedListener listener) {
        taskListeners.remove(listener);
    }


    //----------------------------------------------- 自定义方法 ------------------------------------------------
    private void readAllFunctions() {
        String[] keys = functionMMKV.allKeys();
        if (keys == null) return;

        ArrayList<String> removeKeys = new ArrayList<>();
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
            FunctionSaveReference function = new FunctionSaveReference(functionMMKV, key);
            if (function.get() == null) {
                removeKeys.add(key);
                continue;
            }
            functions.put(key, function);
        }

//        removeKeys.forEach(functionMMKV::remove);
    }

    public ArrayList<Function> getAllFunctions() {
        ArrayList<Function> functions = new ArrayList<>();
        for (Map.Entry<String, FunctionSaveReference> entry : this.functions.entrySet()) {
            functions.add(entry.getValue().get());
        }
        return functions;
    }

    public ArrayList<String> getAllFunctionTags() {
        HashSet<String> tags = new HashSet<>();
        boolean existNoTag = false;
        for (Map.Entry<String, FunctionSaveReference> entry : functions.entrySet()) {
            if (entry.getValue().getTags().isEmpty()) existNoTag = true;
            else tags.addAll(entry.getValue().getTags());
        }
        ArrayList<String> list = new ArrayList<>(tags);
        if (existNoTag || functions.isEmpty()) list.add(NO_TAG);
        return list;
    }

    public LinkedHashMap<String, FunctionPinsAction> getAllFunctionActions() {
        LinkedHashMap<String, FunctionPinsAction> actions = new LinkedHashMap<>();
        for (Map.Entry<String, FunctionSaveReference> entry : functions.entrySet()) {
            FunctionPinsAction action = entry.getValue().getAction();
            if (action != null) actions.put(entry.getKey(), action);
        }
        return actions;
    }

    public Function getFunctionById(String id) {
        FunctionSaveReference reference = functions.get(id);
        if (reference != null) return reference.get();
        return null;
    }

    public ArrayList<Function> getFunctionsByTag(String tag) {
        ArrayList<Function> functions = new ArrayList<>();
        for (Map.Entry<String, FunctionSaveReference> entry : this.functions.entrySet()) {
            if (entry.getValue().existTag(tag)) {
                functions.add(entry.getValue().get());
            }
        }
        return functions;
    }

    public Function getFunction(String parentId, String id) {
        if (parentId == null || parentId.isEmpty()) return getFunctionById(id);
        Task task = getTaskById(parentId);
        if (task == null) return null;
        return task.getFunctionById(id);
    }

    public void saveFunction(Function function) {
        FunctionSaveReference reference = functions.get(function.getId());
        if (reference == null) {
            functions.put(function.getId(), new FunctionSaveReference(functionMMKV, function));
            functionListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onCreated(function));
        } else {
            reference.set(function);
            functionListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onChanged(function));
        }
    }

    public void removeFunction(String id) {
        FunctionSaveReference reference = functions.remove(id);
        if (reference != null) {
            functionListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onRemoved(reference.get()));
            reference.remove();
        }
    }

    public void addFunctionListener(FunctionSaveChangedListener listener) {
        functionListeners.add(listener);
    }

    public void removeFunctionListener(FunctionSaveChangedListener listener) {
        functionListeners.remove(listener);
    }


    //----------------------------------------------- 全局变量 ------------------------------------------------

    private void readAllVariables() {
        String[] keys = variableMMKV.allKeys();
        if (keys == null) return;

        ArrayList<String> removeKeys = new ArrayList<>();
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
            VariableSaveReference var = new VariableSaveReference(variableMMKV, key);
            if (var.get() == null) {
                removeKeys.add(key);
                continue;
            }
            variables.put(key, var);
        }

//        removeKeys.forEach(functionMMKV::remove);
    }

    public HashMap<String, PinValue> getAllVariables() {
        HashMap<String, PinValue> variables = new HashMap<>();
        this.variables.forEach((key, value) -> variables.put(key, value.get()));
        return variables;
    }

    public PinValue getVariable(String key) {
        VariableSaveReference reference = variables.get(key);
        if (reference != null) return reference.get();
        return null;
    }

    // 仅设置
    public void setVariable(String key, PinValue value) {
        VariableSaveReference reference = variables.get(key);
        if (reference != null) {
            reference.set(value);
        }
    }

    public void addVariable(String key, PinValue value) {
        VariableSaveReference reference = variables.get(key);
        if (reference == null) {
            variables.put(key, new VariableSaveReference(variableMMKV, key, value));
            variableListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onCreated(key, value));
        } else {
            reference.set(value);
            variableListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onChanged(key, value));
        }
    }

    public void removeVariable(String key) {
        VariableSaveReference reference = variables.remove(key);
        if (reference != null) {
            variableListeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onChanged(key, reference.get()));
            reference.remove();
        }
    }

    public void addVariableListener(VariableSaveChangedListener listener) {
        variableListeners.add(listener);
    }

    public void removeVariableListener(VariableSaveChangedListener listener) {
        variableListeners.remove(listener);
    }

    //----------------------------------------------- 任务标签 ------------------------------------------------

    public ArrayList<String> getTaskTags() {
        String[] keys = taskTagsMMKV.allKeys();
        ArrayList<String> tags = new ArrayList<>();
        if (keys != null) {
            Collections.addAll(tags, keys);
            Collator collator = Collator.getInstance(Locale.CHINA);
            tags.sort(collator::compare);
        }
        tags.add(SHORTCUT_TAG);
        return tags;
    }

    public void addTaskTag(String tag) {
        taskTagsMMKV.encode(tag, true);
    }

    public void removeTaskTag(String tag) {
        if (tag == null || tag.isEmpty()) return;
        taskTagsMMKV.remove(tag);

        getTasksByTag(tag).forEach(task -> {
            task.removeTag(tag);
            task.save();
        });
    }


    //----------------------------------------------- 方法标签 ------------------------------------------------

    public ArrayList<String> getFunctionTags() {
        String[] keys = functionTagsMMKV.allKeys();
        ArrayList<String> tags = new ArrayList<>();
        if (keys != null) {
            tags.addAll(Arrays.asList(keys));
            Collator collator = Collator.getInstance(Locale.CHINA);
            tags.sort(collator::compare);
        }
        return tags;
    }

    public void addFunctionTag(String tag) {
        functionTagsMMKV.encode(tag, true);
    }

    public void removeFunctionTag(String tag) {
        if (tag == null || tag.isEmpty()) return;
        functionTagsMMKV.remove(tag);

        getFunctionsByTag(tag).forEach(function -> {
            function.removeTag(tag);
            function.save();
        });
    }

    //----------------------------------------------- 日志 ------------------------------------------------
    public void addLog(String taskId, String log) {
        int count = loggerMMKV.decodeInt(taskId, 0);
        count++;
        LogInfo logInfo = new LogInfo(count, log);
        loggerMMKV.encode(taskId, count);
        loggerMMKV.encode(taskId + count, GsonUtils.toJson(logInfo));
        Log.d("TAG", "addLog: " + log);
    }

    public void removeLog(String taskId) {
        int count = loggerMMKV.decodeInt(taskId, 0);
        for (int i = 1; i <= count; i++) {
            loggerMMKV.remove(taskId + i);
        }
        loggerMMKV.remove(taskId);
    }

    public String getLog(String taskId) {
        StringBuilder builder = new StringBuilder();
        int count = loggerMMKV.decodeInt(taskId, 0);
        for (int i = count; i > 0; i--) {
            String json = loggerMMKV.decodeString(taskId + i);
            LogInfo logInfo = GsonUtils.getAsObject(json, LogInfo.class, null);
            if (logInfo != null) {
                builder.append(logInfo.getLogString());
                builder.append("\n\n");
            }
        }
        return builder.toString().trim();
    }
}
