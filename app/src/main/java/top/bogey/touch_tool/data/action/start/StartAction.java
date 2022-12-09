package top.bogey.touch_tool.data.action.start;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.BaseAction;

public class StartAction extends BaseAction {

    public StartAction(String[] tags) {
        super(tags);
    }

    @Override
    public boolean doAction(TaskHelper taskHelper) {
        BaseAction outAction = getOutAction();
        if (outAction == null) return true;
        return outAction.doAction(taskHelper);
    }
}
