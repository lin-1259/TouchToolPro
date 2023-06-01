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

public class DateStateAction extends BaseAction {
    private transient Pin yearPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_year, PinDirection.OUT);
    private transient Pin monthPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_month, PinDirection.OUT);
    private transient Pin dayPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_day, PinDirection.OUT);
    private transient Pin weekdayPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_weekday, PinDirection.OUT);

    public DateStateAction() {
        super(R.string.action_date_state_title);
        yearPin = addPin(yearPin);
        monthPin = addPin(monthPin);
        dayPin = addPin(dayPin);
        weekdayPin = addPin(weekdayPin);
    }

    public DateStateAction(JsonObject jsonObject) {
        super(R.string.action_date_state_title, jsonObject);
        yearPin = reAddPin(yearPin);
        monthPin = reAddPin(monthPin);
        dayPin = reAddPin(dayPin);
        weekdayPin = reAddPin(weekdayPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        ((PinInteger) yearPin.getValue()).setValue(calendar.get(Calendar.YEAR));
        ((PinInteger) monthPin.getValue()).setValue(calendar.get(Calendar.MONTH) + 1);
        ((PinInteger) dayPin.getValue()).setValue(calendar.get(Calendar.DAY_OF_MONTH));
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) + 1 - Calendar.MONDAY;
        if (weekDay <= 0) weekDay += 7;
        ((PinInteger) weekdayPin.getValue()).setValue(weekDay);
    }
}
