package top.bogey.touch_tool_pro.bean.action.pos;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMorePinInterface;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class PosToTouchAction extends Action implements ActionMorePinInterface {
    private final transient Pin morePin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin touchPin = new Pin(new PinTouch(), R.string.pin_touch, true);
    private transient Pin firstPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin secondPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin addPin = new Pin(new PinAdd(morePin, 2), R.string.action_subtitle_add_pin, true);
    private transient Pin timePin = new Pin(new PinValueArea(10, 60000, 10, 300, 300), R.string.action_position_to_touch_subtitle_time);

    public PosToTouchAction() {
        super(ActionType.POS_TO_TOUCH);
        touchPin = addPin(touchPin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
        addPin = addPin(addPin);
        timePin = addPin(timePin);
    }

    public PosToTouchAction(JsonObject jsonObject) {
        super(jsonObject);
        touchPin = reAddPin(touchPin);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
        reAddPin(morePin, 2);
        addPin = reAddPin(addPin);
        timePin = reAddPin(timePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        ArrayList<Pin> pins = calculateMorePins();
        PinValueArea time = (PinValueArea) getPinValue(runnable, context, timePin);
        int everyTime = time.getRandom() / (pins.size() - 1);
        ArrayList<PinTouch.TouchRecord> records = new ArrayList<>();
        MainApplication instance = MainApplication.getInstance();
        for (int i = 0; i < pins.size(); i++) {
            Pin valuePin = pins.get(i);
            PinPoint point = (PinPoint) getPinValue(runnable, context, valuePin);
            PinTouch.TouchRecord record = new PinTouch.TouchRecord(i == 0 ? 0 : everyTime, point.getX(instance), point.getY(instance), i == pins.size() - 1);
            records.add(record);
        }
        touchPin.getValue(PinTouch.class).setRecords(instance, records);
    }

    @Override
    public ArrayList<Pin> calculateMorePins() {
        ArrayList<Pin> pins = new ArrayList<>();
        boolean start = false;
        for (Pin pin : getPins()) {
            if (pin == firstPin) start = true;
            if (pin == addPin) start = false;
            if (start) pins.add(pin);
        }
        return pins;
    }
}
