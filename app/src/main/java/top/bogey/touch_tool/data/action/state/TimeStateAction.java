package top.bogey.touch_tool.data.action.state;

import com.google.gson.JsonObject;

import java.util.Calendar;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinInteger;

public class TimeStateAction extends BaseAction {
    private transient Pin hourPin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_hour, PinDirection.OUT);
    private transient Pin minutePin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_minute, PinDirection.OUT);
    private transient Pin secondPin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_second, PinDirection.OUT);

    public TimeStateAction() {
        super(R.string.action_time_state_title);
        hourPin = addPin(hourPin);
        minutePin = addPin(minutePin);
        secondPin = addPin(secondPin);
    }

    public TimeStateAction(JsonObject jsonObject) {
        super(R.string.action_time_state_title, jsonObject);
        hourPin = reAddPin(hourPin);
        minutePin = reAddPin(minutePin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        ((PinInteger) hourPin.getValue()).setValue(calendar.get(Calendar.HOUR_OF_DAY));
        ((PinInteger) minutePin.getValue()).setValue(calendar.get(Calendar.MINUTE));
        ((PinInteger) secondPin.getValue()).setValue(calendar.get(Calendar.SECOND));
    }
}
