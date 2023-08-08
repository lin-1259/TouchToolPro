package top.bogey.touch_tool_pro.bean.action.pos;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class PosToIntAction extends Action {
    private transient Pin xPin = new Pin(new PinInteger(), R.string.point_x, true);
    private transient Pin yPin = new Pin(new PinInteger(), R.string.point_y, true);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);

    public PosToIntAction() {
        super(ActionType.POS_TO_INT);
        xPin = addPin(xPin);
        yPin = addPin(yPin);
        posPin = addPin(posPin);
    }

    public PosToIntAction(JsonObject jsonObject) {
        super(jsonObject);
        xPin = reAddPin(xPin);
        yPin = reAddPin(yPin);
        posPin = reAddPin(posPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinPoint pos = (PinPoint) getPinValue(runnable, context, posPin);
        xPin.getValue(PinInteger.class).setValue(pos.getX(MainApplication.getInstance()));
        yPin.getValue(PinInteger.class).setValue(pos.getY(MainApplication.getInstance()));
    }
}
