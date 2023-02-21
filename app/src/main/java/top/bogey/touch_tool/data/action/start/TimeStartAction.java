package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
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
    public boolean checkReady(TaskRunnable runnable, ActionContext actionContext) {
        if (getPeriodic() > 0) {
            return true;
        } else {
            return getStartTime() > System.currentTimeMillis();
        }
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
