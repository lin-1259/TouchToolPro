package top.bogey.touch_tool.utils;

import top.bogey.touch_tool.data.Task;

public interface TaskChangedCallback {
    void onCreated(Task task);
    void onChanged(Task task);
    void onRemoved(Task task);
}
