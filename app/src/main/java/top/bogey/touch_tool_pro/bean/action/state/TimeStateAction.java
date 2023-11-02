package top.bogey.touch_tool_pro.bean.action.state;

import com.google.gson.JsonObject;

import java.util.Calendar;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class TimeStateAction extends Action {
    private transient Pin hourPin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_hour, true);
    private transient Pin minutePin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_minute, true);
    private transient Pin secondPin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_second, true);
    private transient Pin timestampPin = new Pin(new PinInteger(), R.string.action_time_state_subtitle_timestamp, true);

    public TimeStateAction() {
        super(ActionType.TIME_STATE);
        hourPin = addPin(hourPin);
        minutePin = addPin(minutePin);
        secondPin = addPin(secondPin);
        timestampPin = addPin(timestampPin);
    }

    public TimeStateAction(JsonObject jsonObject) {
        super(jsonObject);
        hourPin = reAddPin(hourPin);
        minutePin = reAddPin(minutePin);
        secondPin = reAddPin(secondPin);
        timestampPin = reAddPin(timestampPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        hourPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minutePin.getValue(PinInteger.class).setValue(calendar.get(Calendar.MINUTE));
        secondPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.SECOND));
        timestampPin.getValue(PinInteger.class).setValue((int) (calendar.getTimeInMillis() / 1000));
    }
}
