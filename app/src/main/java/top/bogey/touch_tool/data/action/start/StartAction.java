package top.bogey.touch_tool.data.action.start;

import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinSpinnerHelper;
import top.bogey.touch_tool.data.action.pin.PinType;

public class StartAction extends BaseAction {
    protected final Pin<PinSpinnerHelper> restartPin;

    public StartAction(String tag) {
        super(tag);
        addPin(outPin);
        restartPin = new Pin<>(PinType.ARRAY, R.string.task_restart_tips, new PinSpinnerHelper(R.array.restart_type));
    }

    @Override
    public boolean doAction(WorldState worldState, Task task) {
        if (!checkReady(worldState, task)) return false;
        for (Map.Entry<String, String> entry : outPin.getLinks().entrySet()) {
            BaseAction action = task.getActionById(entry.getKey());
            if (action == null) return false;
            return action.doAction(worldState, task);
        }
        return true;
    }
}
