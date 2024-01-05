package top.bogey.touch_tool_pro.save;

import top.bogey.touch_tool_pro.bean.task.Task;

public interface TaskSaveChangedListener {
    void onCreated(Task value);

    void onChanged(Task value);

    void onRemoved(Task value);
}
