package top.bogey.touch_tool_pro.bean.action.pos;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.check.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class PosInAreaAction extends CheckAction {
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);

    public PosInAreaAction() {
        super(ActionType.POS_IN_AREA);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
    }

    public PosInAreaAction(JsonObject jsonObject) {
        super(jsonObject);
        areaPin = reAddPin(areaPin);
        posPin = reAddPin(posPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        PinPoint pos = (PinPoint) getPinValue(runnable, context, posPin);

        MainApplication instance = MainApplication.getInstance();
        result.setBool(area.getArea(instance).contains(pos.getX(instance), pos.getY(instance)));
    }
}
