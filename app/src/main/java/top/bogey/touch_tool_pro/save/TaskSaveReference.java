package top.bogey.touch_tool_pro.save;

import com.tencent.mmkv.MMKV;

import java.util.HashSet;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class TaskSaveReference extends SaveReference<Task> {
    private final HashSet<Class<? extends StartAction>> classHashSet = new HashSet<>();
    private final HashSet<String> tags = new HashSet<>();
    private String taskName;

    public TaskSaveReference(MMKV mmkv, String saveId) {
        super(mmkv, saveId);
    }

    public TaskSaveReference(MMKV mmkv, Task save) {
        super(mmkv, save.getId(), save);
    }

    @Override
    public Task getOrigin() {
        try {
            return (Task) GsonUtils.getAsObject(mmkv.decodeString(saveId), FunctionContext.class, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void set(Task save) {
        super.set(save);
        classHashSet.clear();
        for (Action action : save.getActions()) {
            if (action instanceof StartAction) {
                classHashSet.add(((StartAction) action).getClass());
            }
        }
        tags.clear();
        if (save.getTags() != null) {
            tags.addAll(save.getTags());
        }
        taskName = save.getTitle();
    }

    public boolean existClass(Class<? extends StartAction> actionClass) {
        if (classHashSet.isEmpty()) {
            Task task = get();
            if (task == null) return false;
            for (Action action : task.getActions()) {
                if (action instanceof StartAction) {
                    classHashSet.add(((StartAction) action).getClass());
                }
            }
        }
        return classHashSet.contains(actionClass);
    }

    public boolean existTag(String tag) {
        getTags();
        if (tags.contains(tag)) return true;
        if (tag == null || tag.isEmpty() || SaveRepository.NO_TAG.equals(tag)) {
            return tags.isEmpty();
        }
        return false;
    }

    public String getTaskName() {
        if (taskName == null) {
            Task task = get();
            taskName = task.getTitle();
        }
        return taskName;
    }

    public HashSet<String> getTags() {
        if (tags.isEmpty()) {
            Task task = get();
            if (task == null) return tags;
            if (task.getTags() != null) {
                tags.addAll(task.getTags());
            }
        }
        return tags;
    }
}
