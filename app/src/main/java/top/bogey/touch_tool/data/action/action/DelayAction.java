package top.bogey.touch_tool.data.action.action;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.data.TaskHelper;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.TimeArea;

public class DelayAction extends BaseAction {
    private final TimeArea delay = new TimeArea(0, TimeUnit.MILLISECONDS);

    public DelayAction() {
        super(new String[]{ActionTag.ACTION_DELAY});
    }

    @Override
    public boolean doAction(TaskHelper taskHelper) {
        sleep(delay.getRandomTime());
        return true;
    }

    @Override
    public boolean checkState(TaskHelper taskHelper) {
        return super.checkState(taskHelper);
    }

    @Override
    public boolean isValid() {
        return delay.getMin() + delay.getMax() > 0;
    }

    public TimeArea getDelay() {
        return delay;
    }
}
