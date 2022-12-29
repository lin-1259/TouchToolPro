package top.bogey.touch_tool.data;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import top.bogey.touch_tool.data.action.start.StartAction;

public class TaskRepository {
    private static TaskRepository repository;
    private final static String TASK_DB = "TASK_DB";
    private final static MMKV taskMMKV = MMKV.mmkvWithID(TASK_DB, MMKV.SINGLE_PROCESS_MODE, TASK_DB);

    private final LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();

    public static TaskRepository getInstance() {
        if (repository == null) {
            repository = new TaskRepository();
            repository.readAllTasks();
        }
        return repository;
    }

    private void readAllTasks() {
        String[] keys = taskMMKV.allKeys();
        if (keys == null) return;
        for (String key : keys) {
            Task task = taskMMKV.decodeParcelable(key, Task.class);
            if (task == null) continue;
            tasks.put(key, task);
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(String id) {
        return tasks.get(id);
    }

    public ArrayList<Task> getTasksByStart(Class<? extends StartAction> startActionClass) {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : tasks.values()) {
            StartAction startAction = task.getStartAction(startActionClass);
            if (startAction == null) continue;
            taskArrayList.add(task);
        }
        return taskArrayList;
    }

    public void saveTask(Task task) {
        taskMMKV.encode(task.getId(), task);
        tasks.put(task.getId(), task);
    }
}
