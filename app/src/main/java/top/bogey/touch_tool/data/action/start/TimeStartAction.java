package top.bogey.touch_tool.data.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinLong;
import top.bogey.touch_tool.utils.AppUtils;

public class TimeStartAction extends StartAction {
    private transient Pin datePin = new Pin(new PinLong(System.currentTimeMillis()), R.string.action_time_start_subtitle_date, PinSubType.DATE);
    private transient Pin timePin = new Pin(new PinLong(System.currentTimeMillis()), R.string.action_time_start_subtitle_time, PinSubType.TIME);
    private transient Pin periodicPin = new Pin(new PinLong(0), R.string.action_time_start_subtitle_periodic, PinSubType.PERIODIC);

    public TimeStartAction() {
        super(R.string.action_time_start_title);
        datePin = addPin(datePin);
        timePin = addPin(timePin);
        periodicPin = addPin(periodicPin);
    }

    public TimeStartAction(JsonObject jsonObject) {
        super(R.string.action_time_start_title, jsonObject);
        datePin = reAddPin(datePin);
        timePin = reAddPin(timePin);
        periodicPin = reAddPin(periodicPin);
    }

    public long getStartTime() {
        long date = ((PinLong) datePin.getValue()).getValue();
        long time = ((PinLong) timePin.getValue()).getValue();
        return AppUtils.mergeDateTime(date, time);
    }

    public long getPeriodic() {
        return ((PinLong) periodicPin.getValue()).getValue();
    }
}
