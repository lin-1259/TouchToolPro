package top.bogey.touch_tool.data.action.start;

import java.util.concurrent.atomic.AtomicLong;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.ActionTag;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinType;
import top.bogey.touch_tool.utils.AppUtils;

public class TimeStartAction extends StartAction {
    private final Pin<AtomicLong> datePin;
    private final Pin<AtomicLong> timePin;
    private final Pin<AtomicLong> periodicPin;

    public TimeStartAction() {
        super(ActionTag.START_TIME);
        long timeMillis = System.currentTimeMillis();
        datePin = addPin(new Pin<>(PinType.DATE, R.string.time_condition_start_date, new AtomicLong(timeMillis)));
        timePin = addPin(new Pin<>(PinType.TIME, R.string.time_condition_start_time, new AtomicLong(timeMillis)));
        periodicPin = addPin(new Pin<>(PinType.PERIODIC, R.string.time_condition_periodic, new AtomicLong(0)));
        addPin(restartPin);
        titleId = R.string.task_type_time;
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        long date = datePin.getValue().get();
        long time = timePin.getValue().get();
        long periodic = periodicPin.getValue().get();
        if (periodic > 0) {
            return true;
        } else {
            return AppUtils.mergeDateTime(date, time) > System.currentTimeMillis();
        }
    }
}
