package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StartAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinLong;
import top.bogey.touch_tool.utils.AppUtils;

public class TimeStartAction extends StartAction {
    private transient final Pin datePin;
    private transient final Pin timePin;
    private transient final Pin periodicPin;

    public TimeStartAction(Context context) {
        super(context, R.string.action_time_start_title);
        long timeMillis = System.currentTimeMillis();
        datePin = addPin(new Pin(new PinLong(timeMillis), context.getString(R.string.action_time_start_subtitle_date), PinSubType.DATE));
        timePin = addPin(new Pin(new PinLong(timeMillis), context.getString(R.string.action_time_start_subtitle_time), PinSubType.TIME));
        periodicPin = addPin(new Pin(new PinLong(0), context.getString(R.string.action_time_start_subtitle_periodic), PinSubType.PERIODIC));
    }

    public TimeStartAction(JsonObject jsonObject) {
        super(jsonObject);
        datePin = addPin(tmpPins.remove(0));
        timePin = addPin(tmpPins.remove(0));
        periodicPin = addPin(tmpPins.remove(0));
    }

    @Override
    public boolean checkReady(WorldState worldState, Task task) {
        long date = ((PinLong) getPinValue(worldState, task, datePin)).getValue();
        long time = ((PinLong) getPinValue(worldState, task, timePin)).getValue();
        long periodic = ((PinLong) getPinValue(worldState, task, periodicPin)).getValue();
        if (periodic > 0) {
            return true;
        } else {
            return AppUtils.mergeDateTime(date, time) > System.currentTimeMillis();
        }
    }

    public long getStartTime(WorldState worldState, Task task) {
        long date = ((PinLong) getPinValue(worldState, task, datePin)).getValue();
        long time = ((PinLong) getPinValue(worldState, task, timePin)).getValue();
        return AppUtils.mergeDateTime(date, time);
    }

    public long getPeriodic(WorldState worldState, Task task) {
        return ((PinLong) getPinValue(worldState, task, periodicPin)).getValue();
    }
}
