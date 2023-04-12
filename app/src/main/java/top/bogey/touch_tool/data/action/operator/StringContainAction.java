package top.bogey.touch_tool.data.action.operator;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.CalculateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinString;

public class StringContainAction extends CalculateAction {
    protected transient Pin outValuePin = new Pin(new PinBoolean(), PinDirection.OUT);
    protected transient Pin originPin = new Pin(new PinString(), R.string.action_string_contain_operator_subtitle_origin);
    protected transient Pin matchPin = new Pin(new PinString(), R.string.action_string_contain_operator_title_match);

    public StringContainAction() {
        super(R.string.action_string_contain_operator_title);
        outValuePin = addPin(outValuePin);
        originPin = addPin(originPin);
        matchPin = addPin(matchPin);
    }

    public StringContainAction(JsonObject jsonObject) {
        super(R.string.action_string_contain_operator_title, jsonObject);
        outValuePin = reAddPin(outValuePin);
        originPin = reAddPin(originPin);
        matchPin = reAddPin(matchPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(outValuePin.getId())) return;
        PinBoolean value = (PinBoolean) outValuePin.getValue();
        value.setValue(false);

        String origin = ((PinString) getPinValue(runnable, actionContext, originPin)).getValue();
        String match = ((PinString) getPinValue(runnable, actionContext, matchPin)).getValue();
        if (origin != null && match != null) {
            Pattern pattern = Pattern.compile(origin);
            Matcher matcher = pattern.matcher(match);
            value.setValue(matcher.find());
        }
    }
}
