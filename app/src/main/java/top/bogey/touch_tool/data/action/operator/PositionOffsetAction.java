package top.bogey.touch_tool.data.action.operator;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class PositionOffsetAction extends CalculateAction {
    private transient Pin outValuePin = new Pin(new PinPoint(), R.string.action_state_subtitle_state, PinDirection.OUT);
    private transient Pin firstPin = new Pin(new PinPoint(), R.string.action_position_add_operator_subtitle_position);
    private transient Pin secondPin = new Pin(new PinPoint(), R.string.action_position_add_operator_subtitle_offset);

    public PositionOffsetAction() {
        super(R.string.action_position_add_operator_title);
        outValuePin = addPin(outValuePin);
        firstPin = addPin(firstPin);
        secondPin = addPin(secondPin);
    }

    public PositionOffsetAction(JsonObject jsonObject) {
        super(R.string.action_position_add_operator_title, jsonObject);
        outValuePin = reAddPin(outValuePin);
        firstPin = reAddPin(firstPin);
        secondPin = reAddPin(secondPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinPoint value = (PinPoint) outValuePin.getValue();

        PinPoint first = (PinPoint) getPinValue(runnable, actionContext, firstPin);
        PinPoint second = (PinPoint) getPinValue(runnable, actionContext, secondPin);
        value.setX(first.getX() + second.getX());
        value.setY(first.getY() + second.getY());
    }
}
