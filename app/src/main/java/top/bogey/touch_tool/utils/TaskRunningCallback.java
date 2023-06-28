package top.bogey.touch_tool.utils;

import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;

public interface TaskRunningCallback {
    void onStart(TaskRunnable runnable);

    void onEnd(TaskRunnable runnable);

    void onProgress(TaskRunnable runnable, int progress);

    void onAction(TaskRunnable runnable, ActionContext context, BaseAction action, int progress);
}
