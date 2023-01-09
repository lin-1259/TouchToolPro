package top.bogey.touch_tool.utils;

import top.bogey.touch_tool.data.TaskRunnable;

public interface TaskRunningCallback {
    void onStart(TaskRunnable runnable);

    void onEnd(TaskRunnable runnable);

    void onProgress(TaskRunnable runnable, int progress);
}
