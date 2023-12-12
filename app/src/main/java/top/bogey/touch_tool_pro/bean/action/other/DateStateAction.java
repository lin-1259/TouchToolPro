package top.bogey.touch_tool_pro.bean.action.other;

import com.google.gson.JsonObject;

import java.util.Calendar;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class DateStateAction extends Action {
    private transient Pin yearPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_year, true);
    private transient Pin monthPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_month, true);
    private transient Pin dayPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_day, true);
    private transient Pin weekPin = new Pin(new PinInteger(), R.string.action_date_state_subtitle_weekday, true);

    public DateStateAction() {
        super(ActionType.DATE_STATE);
        yearPin = addPin(yearPin);
        monthPin = addPin(monthPin);
        dayPin = addPin(dayPin);
        weekPin = addPin(weekPin);
    }

    public DateStateAction(JsonObject jsonObject) {
        super(jsonObject);
        yearPin = reAddPin(yearPin);
        monthPin = reAddPin(monthPin);
        dayPin = reAddPin(dayPin);
        weekPin = reAddPin(weekPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        yearPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.YEAR));
        monthPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.MONTH) + 1);
        dayPin.getValue(PinInteger.class).setValue(calendar.get(Calendar.DAY_OF_MONTH));
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) + 1 - Calendar.MONDAY;
        if (weekDay <= 0) weekDay += 7;
        weekPin.getValue(PinInteger.class).setValue(weekDay);
    }
}
