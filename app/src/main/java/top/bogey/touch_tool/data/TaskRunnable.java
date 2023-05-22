package top.bogey.touch_tool.data;

import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.Future;

import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class TaskRunnable implements Runnable {
    private final Task startTask;
    private final ActionContext actionContext;
    private final StartAction startAction;

    private final HashSet<TaskRunningCallback> callbacks = new HashSet<>();
    private int progress = 0;
    private boolean interrupt = false;

    private Future<?> future;

    public TaskRunnable(Task startTask, StartAction startAction, ActionContext actionContext) {
        this.startTask = startTask;
        this.actionContext = actionContext;
        this.startAction = startAction;
    }

    public void stop() {
        if (future != null) {
            future.cancel(true);
        }
        interrupt = true;
    }

    public boolean isInterrupt() {
        return interrupt;
    }

    @Override
    public void run() {
        try {
            if (!startAction.checkReady(this, actionContext)) return;
            callbacks.stream().filter(Objects::nonNull).forEach(taskRunningCallback -> taskRunningCallback.onStart(this));
            startAction.doAction(this, actionContext, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        callbacks.stream().filter(Objects::nonNull).forEach(taskRunningCallback -> taskRunningCallback.onEnd(this));
    }

    public void addCallback(TaskRunningCallback callback) {
        callbacks.add(callback);
    }

    public boolean addProgress() {
        progress++;
        callbacks.stream().filter(Objects::nonNull).forEach(taskRunningCallback -> taskRunningCallback.onProgress(this, progress));
        if (startAction.checkStop(this, startTask)) {
            stop();
            return false;
        }
        return true;
    }

    public Task getStartTask() {
        return startTask;
    }

    public StartAction getStartAction() {
        return startAction;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
