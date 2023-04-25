package top.bogey.touch_tool.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.Future;

import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class TaskRunnable implements Runnable {
    private final Task task;
    private final StartAction startAction;

    private final HashSet<TaskRunningCallback> callbacks = new HashSet<>();
    private int progress = 0;
    private boolean interrupt = false;

    private Future<?> future;

    public TaskRunnable(Task task, StartAction startAction) {
        this.task = task;
        this.startAction = startAction;
    }

    public void stop() {
        future.cancel(true);
        interrupt = true;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    @Override
    public void run() {
        try {
            if (!startAction.checkReady(this, task)) return;
            callbacks.stream().filter(Objects::nonNull).forEach(taskRunningCallback -> taskRunningCallback.onStart(this));
            startAction.doAction(this, task, null);
        } catch (Exception e) {
            e.printStackTrace();
            TaskRepository.getInstance().addLog(task, "\n", e.toString());
        }
        callbacks.stream().filter(Objects::nonNull).forEach(taskRunningCallback -> taskRunningCallback.onEnd(this));
    }

    public void addCallback(TaskRunningCallback callback) {
        callbacks.add(callback);
    }

    public boolean addProgress() {
        progress++;
        callbacks.stream().filter(Objects::nonNull).forEach(taskRunningCallback -> taskRunningCallback.onProgress(this, progress));
        if (startAction.checkStop(this, task)) {
            stop();
            return false;
        }
        return true;
    }

    public Task getTask() {
        return task;
    }

    public StartAction getStartAction() {
        return startAction;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
