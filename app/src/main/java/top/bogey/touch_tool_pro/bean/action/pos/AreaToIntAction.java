package top.bogey.touch_tool_pro.bean.action.pos;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class AreaToIntAction extends Action {
    private transient Pin leftPin = new Pin(new PinInteger(), R.string.area_left, true);
    private transient Pin topPin = new Pin(new PinInteger(), R.string.area_top, true);
    private transient Pin rightPin = new Pin(new PinInteger(), R.string.area_right, true);
    private transient Pin bottomPin = new Pin(new PinInteger(), R.string.area_bottom, true);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);

    public AreaToIntAction() {
        super(ActionType.AREA_TO_INT);
        leftPin = addPin(leftPin);
        topPin = addPin(topPin);
        rightPin = addPin(rightPin);
        bottomPin = addPin(bottomPin);
        areaPin = addPin(areaPin);
    }

    public AreaToIntAction(JsonObject jsonObject) {
        super(jsonObject);
        leftPin = reAddPin(leftPin);
        topPin = reAddPin(topPin);
        rightPin = reAddPin(rightPin);
        bottomPin = reAddPin(bottomPin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Rect rect = area.getArea(MainApplication.getInstance());
        leftPin.getValue(PinInteger.class).setValue(rect.left);
        topPin.getValue(PinInteger.class).setValue(rect.top);
        rightPin.getValue(PinInteger.class).setValue(rect.right);
        bottomPin.getValue(PinInteger.class).setValue(rect.bottom);
    }
}
