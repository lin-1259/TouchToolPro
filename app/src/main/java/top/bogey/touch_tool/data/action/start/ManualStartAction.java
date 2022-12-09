package top.bogey.touch_tool.data.action.start;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;

public class ManualStartAction extends StartAction {
    private final Map<CharSequence, List<CharSequence>> packages = new LinkedHashMap<>();

    public ManualStartAction() {
        super(new String[]{ActionTag.START_MANUAL});
    }

    @Override
    public boolean checkState(TaskHelper taskHelper) {
        List<CharSequence> activityClasses = packages.get(taskHelper.getPackageName());
        if (activityClasses == null) return false;
        if (activityClasses.isEmpty()) return true;
        return activityClasses.contains(taskHelper.getActivityName());
    }

    @Override
    public boolean isValid() {
        return !packages.isEmpty();
    }

    public Map<CharSequence, List<CharSequence>> getPackages() {
        return packages;
    }
}
