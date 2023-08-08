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

public class PosFromIntAction extends Action {
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin xPin = new Pin(new PinInteger(), R.string.point_x);
    private transient Pin yPin = new Pin(new PinInteger(), R.string.point_y);

    public PosFromIntAction() {
        super(ActionType.POS_FROM_INT);
        posPin = addPin(posPin);
        xPin = addPin(xPin);
        yPin = addPin(yPin);
    }

    public PosFromIntAction(JsonObject jsonObject) {
        super(jsonObject);
        posPin = reAddPin(posPin);
        xPin = reAddPin(xPin);
        yPin = reAddPin(yPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinInteger x = (PinInteger) getPinValue(runnable, context, xPin);
        PinInteger y = (PinInteger) getPinValue(runnable, context, yPin);
        posPin.getValue(PinPoint.class).setPoint(MainApplication.getInstance(), x.getValue(), y.getValue());
    }
}
