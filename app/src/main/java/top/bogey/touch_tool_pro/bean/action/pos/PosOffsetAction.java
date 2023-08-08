package top.bogey.touch_tool_pro.bean.action.pos;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class PosOffsetAction extends Action {
    private transient Pin resultPin = new Pin(new PinPoint(), R.string.pin_point, true);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin offsetPin = new Pin(new PinPoint(), R.string.action_position_offset_subtitle_offset);

    public PosOffsetAction() {
        super(ActionType.POS_OFFSET);
        resultPin = addPin(resultPin);
        posPin = addPin(posPin);
        offsetPin = addPin(offsetPin);
    }

    public PosOffsetAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
        posPin = reAddPin(posPin);
        offsetPin = reAddPin(offsetPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinPoint result = resultPin.getValue(PinPoint.class);

        PinPoint pos = (PinPoint) getPinValue(runnable, context, posPin);
        PinPoint offset = (PinPoint) getPinValue(runnable, context, offsetPin);

        MainApplication instance = MainApplication.getInstance();
        result.setPoint(instance, pos.getX(instance) + offset.getX(instance), pos.getY(instance) + offset.getY(instance));
    }
}
