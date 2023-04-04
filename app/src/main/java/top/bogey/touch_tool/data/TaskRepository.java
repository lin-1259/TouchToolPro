package top.bogey.touch_tool.data;

import android.content.Context;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.utils.GsonUtils;
import top.bogey.touch_tool.utils.TaskChangedCallback;

public class TaskRepository {
    private static TaskRepository repository;
    private final static String TASK_DB = "TASK_DB";
    private final static MMKV taskMMKV = MMKV.mmkvWithID(TASK_DB, MMKV.SINGLE_PROCESS_MODE);

    private final static String FUNCTION_DB = "FUNCTION_DB";
    private final static MMKV functionMMKV = MMKV.mmkvWithID(FUNCTION_DB, MMKV.SINGLE_PROCESS_MODE);

    private final static String LOG_DB = "LOG_DB";
    private final static MMKV loggerMMKV = MMKV.mmkvWithID(LOG_DB, MMKV.MULTI_PROCESS_MODE);

    private final static String TAGS = "TAGS";
    private final static MMKV tagsMMKV = MMKV.mmkvWithID(TAGS, MMKV.SINGLE_PROCESS_MODE);

    private final LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();
    private final HashSet<TaskChangedCallback> callbacks = new HashSet<>();

    private final LinkedHashMap<String, BaseFunction> functions = new LinkedHashMap<>();

    public static TaskRepository getInstance() {
        if (repository == null) {
            repository = new TaskRepository();
            repository.readAllTasks();
            repository.readAllFunctions();
        }
        return repository;
    }

    private void readAllTasks() {
        String[] keys = taskMMKV.allKeys();
        if (keys == null) return;

        ArrayList<String> removeKeys = new ArrayList<>();
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
            Task task = getOriginTaskById(key);
            if (task == null) {
                removeKeys.add(key);
                continue;
            }
            tasks.put(key, task);
        }

        removeKeys.forEach(taskMMKV::remove);
    }

    private void readAllFunctions() {
        String[] keys = functionMMKV.allKeys();
        if (keys == null) return;

        ArrayList<String> removeKeys = new ArrayList<>();
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
            BaseFunction function = getOriginFunctionById(key);
            if (function == null) {
                removeKeys.add(key);
                continue;
            }
            functions.put(key, function);
        }

        removeKeys.forEach(functionMMKV::remove);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(String id) {
        return tasks.get(id);
    }

    public Task getOriginTaskById(String id) {
        Task task = GsonUtils.getAsClass(taskMMKV.decodeString(id), Task.class, null);
        if (task != null) task.getActions().remove(null);
        return task;
    }

    public ArrayList<Task> getTasksByStart(Class<? extends StartAction> startActionClass) {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : tasks.values()) {
            ArrayList<StartAction> startActions = task.getStartActions(startActionClass);
            if (startActions.size() == 0) continue;
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public ArrayList<Task> getTasksByTag(String tag) {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : tasks.values()) {
            if ((tag == null && task.getTag() == null) || (tag != null && tag.equals(task.getTag()))) {
                taskArrayList.add(task);
            }
        }
        return taskArrayList;
    }

    public void addCallback(TaskChangedCallback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(TaskChangedCallback callback) {
        callbacks.remove(callback);
    }

    public void saveTask(Task task) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) {
            service.replaceWork(task);
        }

        taskMMKV.encode(task.getId(), GsonUtils.toJson(task));
        Task lastTask = tasks.put(task.getId(), task);
        if (lastTask == null) {
            callbacks.stream().filter(Objects::nonNull).forEach(callback -> callback.onCreated(task));
        } else {
            callbacks.stream().filter(Objects::nonNull).forEach(callback -> callback.onChanged(task));
        }
    }

    public void removeTask(String id) {
        taskMMKV.remove(id);
        Task removedTask = tasks.remove(id);
        if (removedTask != null) {
            callbacks.stream().filter(Objects::nonNull).forEach(callback -> callback.onRemoved(removedTask));
            removeLog(removedTask);

            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null) service.stopTask(removedTask);
        }
    }

    public void saveFunction(BaseFunction function) {
        functionMMKV.encode(function.getFunctionId(), GsonUtils.toJson(function));
        functions.put(function.getFunctionId(), function);
    }

    public void removeFunction(String id) {
        functionMMKV.remove(id);
        functions.remove(id);
    }

    public BaseFunction getFunctionById(String id) {
        return functions.get(id);
    }

    public BaseFunction getOriginFunctionById(String id) {
        BaseFunction function = (BaseFunction) GsonUtils.getAsClass(functionMMKV.decodeString(id), BaseAction.class, null);
        if (function != null) function.getActions().remove(null);
        return function;
    }

    public ArrayList<BaseFunction> getFunctions() {
        return new ArrayList<>(functions.values());
    }


    public ArrayList<String> getTags(Context context) {
        String[] keys = tagsMMKV.allKeys();
        ArrayList<String> tags = new ArrayList<>(Collections.singletonList(context.getString(R.string.tag_no)));
        if (keys != null) {
            for (int i = keys.length - 1; i >= 0; i--) {
                String key = keys[i];
                tags.add(tags.size() - 1, key);
            }
        }
        return tags;
    }

    public void addTag(String tag) {
        tagsMMKV.encode(tag, true);
    }

    public void removeTag(String tag) {
        if (tag == null || tag.isEmpty()) return;
        tasks.forEach((id, task) -> {
            if (tag.equals(task.getTag())) {
                task.setTag(null);
                saveTask(task);
            }
        });

        tagsMMKV.remove(tag);
    }

    public void addLog(Task task, String action, String log) {
        LogInfo logInfo = new LogInfo(task.getId(), action + ":" + log);
        loggerMMKV.encode(logInfo.getId(), GsonUtils.toJson(logInfo));
    }

    public void removeLog(Task task) {
        String[] keys = loggerMMKV.allKeys();
        if (keys != null) {
            for (String key : keys) {
                LogInfo logInfo = GsonUtils.getAsClass(loggerMMKV.decodeString(key), LogInfo.class, null);
                if (logInfo != null && task.getId().equals(logInfo.getTaskId())) {
                    loggerMMKV.remove(key);
                }
            }
        }
    }

    public String getLogs(Context context, Task task) {
        ArrayList<LogInfo> infoList = new ArrayList<>();
        String[] keys = loggerMMKV.allKeys();
        if (keys != null) {
            for (String key : keys) {
                LogInfo logInfo = GsonUtils.getAsClass(loggerMMKV.decodeString(key), LogInfo.class, null);
                if (logInfo != null && task.getId().equals(logInfo.getTaskId())) {
                    infoList.add(logInfo);
                }
            }
        }
        infoList.sort((a, b) -> (int) (b.getTime() - a.getTime()));

        StringBuilder builder = new StringBuilder();
        for (LogInfo logInfo : infoList) {
            builder.append(logInfo.getLog(context));
            builder.append("\n");
        }

        return builder.toString().trim();
    }
}
