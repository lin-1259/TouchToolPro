package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class DelayAction extends NormalAction {
    private transient Pin delayPin = new Pin(new PinValueArea(10, 60000, 10, 300, 300), R.string.action_delay_action_subtitle_time);

    public DelayAction() {
        super(ActionType.DELAY);
        delayPin = addPin(delayPin);
    }

    public DelayAction(JsonObject jsonObject) {
        super(jsonObject);
        delayPin = reAddPin(delayPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinValueArea delay = (PinValueArea) getPinValue(runnable, context, delayPin);
        runnable.sleep(delay.getRandom());
        executeNext(runnable, context, outPin);
    }

    public Pin getDelayPin() {
        return delayPin;
    }
}
