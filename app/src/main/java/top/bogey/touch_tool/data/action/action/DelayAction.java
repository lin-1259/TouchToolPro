package top.bogey.touch_tool.data.action.action;

import java.util.concurrent.TimeUnit;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.TimeArea;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinType;

public class DelayAction extends NormalAction {
    private final Pin<TimeArea> delayPin;

    public DelayAction() {
        super(ActionTag.ACTION_DELAY);
        delayPin = addPin(new Pin<>(PinType.TIME_AREA, new TimeArea(300, TimeUnit.MILLISECONDS)));
        titleId = R.string.action_type_delay;
    }

    @Override
    public boolean doAction(WorldState worldState, Task task) {
        TimeArea timeArea = delayPin.getValue();
        return sleep(timeArea.getRandomTime());
    }
}
