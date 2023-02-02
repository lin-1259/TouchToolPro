package top.bogey.touch_tool.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.TaskChangedCallback;

public class TaskRepository {
    private static TaskRepository repository;
    private final static String TASK_DB = "TASK_DB";
    private final static MMKV taskMMKV = MMKV.mmkvWithID(TASK_DB, MMKV.SINGLE_PROCESS_MODE);
    private final static String LOG_DB = "LOG_DB";
    private final static MMKV logMMKV = MMKV.mmkvWithID(LOG_DB, MMKV.SINGLE_PROCESS_MODE);

    private final LinkedHashMap<String, Task> tasks = new LinkedHashMap<>();
    private final HashSet<TaskChangedCallback> callbacks = new HashSet<>();

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
        for (int i = keys.length - 1; i >= 0; i--) {
            String key = keys[i];
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

    public Task getOriginTaskById(String id) {
        return taskMMKV.decodeParcelable(id, Task.class);
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
        MainAccessibilityService service = MainApplication.getService();
        if (service != null && service.isServiceEnabled()) {
            service.replaceWork(task);
        }

        taskMMKV.encode(task.getId(), task);
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
        }
    }

    public void removeTag(String tag) {
        if (tag == null || tag.isEmpty()) return;
        tasks.forEach((id, task) -> {
            if (tag.equals(task.getTag())) {
                task.setTag(null);
                saveTask(task);
            }
        });

        SettingSave.getInstance().removeTag(tag);
    }

    public void addLog(Task task, String action, String log) {
        LogInfo logInfo = new LogInfo(task.getId(), action + ":" + log);
        logMMKV.encode(logInfo.getId(), logInfo);
    }

    public void removeLog(Task task) {
        String[] keys = logMMKV.allKeys();
        if (keys != null) {
            for (String key : keys) {
                LogInfo logInfo = logMMKV.decodeParcelable(key, LogInfo.class);
                if (logInfo != null && task.getId().equals(logInfo.getTaskId())) {
                    logMMKV.remove(key);
                }
            }
        }
    }

    public String getLogs(Context context, Task task) {
        ArrayList<LogInfo> infoList = new ArrayList<>();
        String[] keys = logMMKV.allKeys();
        if (keys != null) {
            for (String key : keys) {
                LogInfo logInfo = logMMKV.decodeParcelable(key, LogInfo.class);
                if (logInfo != null && task.getId().equals(logInfo.getTaskId())) {
                    infoList.add(logInfo);
                }
            }
        }
        infoList.sort((a, b) -> (int) (b.time - a.time));

        StringBuilder builder = new StringBuilder();
        for (LogInfo logInfo : infoList) {
            builder.append(logInfo.getLog(context));
            builder.append("\n");
        }

        return builder.toString().trim();
    }

    public static class LogInfo implements Parcelable {
        private final String id;
        private final long time;
        private final String taskId;
        private final String log;

        public LogInfo(String taskId, String log) {
            id = UUID.randomUUID().toString();
            time = System.currentTimeMillis();
            this.taskId = taskId;
            this.log = log;
        }

        protected LogInfo(Parcel in) {
            id = in.readString();
            time = in.readLong();
            taskId = in.readString();
            log = in.readString();
        }

        public static final Creator<LogInfo> CREATOR = new Creator<LogInfo>() {
            @Override
            public LogInfo createFromParcel(Parcel in) {
                return new LogInfo(in);
            }

            @Override
            public LogInfo[] newArray(int size) {
                return new LogInfo[size];
            }
        };

        public String getId() {
            return id;
        }

        public long getTime() {
            return time;
        }

        public String getTime(Context context) {
            return context.getString(R.string.date, AppUtils.formatDateLocalDate(context, time), AppUtils.formatDateLocalMillisecond(context, time));
        }

        public String getTaskId() {
            return taskId;
        }

        public String getLog(Context context) {
            return getTime(context) + "\t" + log;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeLong(time);
            dest.writeString(taskId);
            dest.writeString(log);
        }
    }
}
