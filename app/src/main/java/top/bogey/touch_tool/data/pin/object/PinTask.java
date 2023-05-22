package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinTask extends PinValue {
    private String taskId;
    private String startId;

    public PinTask() {
        super();
    }

    public PinTask(JsonObject jsonObject) {
        super(jsonObject);
        taskId = GsonUtils.getAsString(jsonObject, "taskId", null);
        startId = GsonUtils.getAsString(jsonObject, "startId", null);
    }

    @Override
    public boolean isEmpty() {
        return taskId == null || taskId.isEmpty() || startId == null || startId.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        Task task = getTask();
        if (task == null) return "";
        return task.getTitle();
    }

    @Override
    public int getPinColor(Context context) {
        return super.getPinColor(context);
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStartId() {
        return startId;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public Task getTask() {
        if (isEmpty()) return null;
        return TaskRepository.getInstance().getTaskById(taskId);
    }

    public StartAction getStartAction() {
        Task task = getTask();
        if (task == null) return null;
        return (StartAction) task.getActionById(startId);
    }
}
