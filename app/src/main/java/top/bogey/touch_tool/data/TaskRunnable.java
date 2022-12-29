package top.bogey.touch_tool.data;

import java.util.HashSet;

import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class TaskRunnable implements Runnable {
    private final Task task;
    private final StartAction startAction;

    private final HashSet<TaskRunningCallback> callbacks = new HashSet<>();
    private int progress = 0;

    public TaskRunnable(Task task, StartAction startAction) {
        this.task = task;
        this.startAction = startAction;
    }

    @Override
    public void run() {
        callbacks.forEach(taskRunningCallback -> taskRunningCallback.onStart(this));
        boolean result = startAction.doAction(WorldState.getInstance(), this);
        callbacks.forEach(taskRunningCallback -> taskRunningCallback.onEnd(this, result));
    }

    public void addCallback(TaskRunningCallback callback) {
        callbacks.add(callback);
    }

    public void addProgress() {
        progress ++;
        callbacks.forEach(taskRunningCallback -> taskRunningCallback.onProgress(this, progress));
    }

    public Task getTask() {
        return task;
    }

    public StartAction getStartAction() {
        return startAction;
    }
}
