package top.bogey.touch_tool.data.action.convert;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;

public class BoolConvertToNot extends CalculateAction {
    private transient Pin outConditionPin = new Pin(new PinBoolean(), R.string.action_state_subtitle_state, PinDirection.OUT);
    private transient Pin conditionPin = new Pin(new PinBoolean(), R.string.action_bool_convert_and_subtitle_condition);

    public BoolConvertToNot() {
        super(R.string.action_bool_convert_not_title);
        outConditionPin = addPin(outConditionPin);
        conditionPin = addPin(conditionPin);
    }

    public BoolConvertToNot(JsonObject jsonObject) {
        super(R.string.action_bool_convert_not_title, jsonObject);
        outConditionPin = reAddPin(outConditionPin);
        conditionPin = reAddPin(conditionPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) outConditionPin.getValue();
        PinBoolean resultPin = (PinBoolean) getPinValue(runnable, actionContext, conditionPin);
        value.setValue(!resultPin.getValue());
    }
}
