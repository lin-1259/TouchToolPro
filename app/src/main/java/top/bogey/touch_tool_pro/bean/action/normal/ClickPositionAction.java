package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ClickPositionAction extends NormalAction {
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin timePin = new Pin(new PinValueArea(10, 60000, 10, 100, 100), R.string.action_touch_pos_action_subtitle_time);
    private transient Pin offsetPin = new Pin(new PinInteger(), R.string.action_touch_pos_action_subtitle_offset);

    public ClickPositionAction() {
        super(ActionType.CLICK_POSITION);
        posPin = addPin(posPin);
        timePin = addPin(timePin);
        offsetPin = addPin(offsetPin);
    }

    public ClickPositionAction(JsonObject jsonObject) {
        super(jsonObject);
        posPin = reAddPin(posPin);
        timePin = reAddPin(timePin);
        offsetPin = reAddPin(offsetPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinPoint pos = (PinPoint) getPinValue(runnable, context, posPin);
        PinValueArea time = (PinValueArea) getPinValue(runnable, context, timePin);
        PinInteger offset = (PinInteger) getPinValue(runnable, context, offsetPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        service.runGesture(pos.getX(service, offset.getValue()), pos.getY(service, offset.getValue()), time.getRandom(), result -> runnable.resume());
        runnable.pause();
        executeNext(runnable, context, outPin);
    }

    public Pin getPosPin() {
        return posPin;
    }
}
