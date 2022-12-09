package top.bogey.touch_tool.data.action.start;

import java.util.List;
import java.util.Map;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;

public class AppStartAction extends StartAction {
    private Map<CharSequence, List<CharSequence>> packages;

    public AppStartAction() {
        super(new String[]{ActionTag.START_APP});
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
        return !(packages == null || packages.isEmpty());
    }

    public Map<CharSequence, List<CharSequence>> getPackages() {
        return packages;
    }

    public void setPackages(Map<CharSequence, List<CharSequence>> packages) {
        this.packages = packages;
    }
}
