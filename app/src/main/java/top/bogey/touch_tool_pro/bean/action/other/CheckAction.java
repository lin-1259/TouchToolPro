package top.bogey.touch_tool_pro.bean.action.other;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;

public abstract class CheckAction extends Action {
    protected transient Pin resultPin = new Pin(new PinBoolean(false), R.string.action_check_subtitle_result, true);

    public CheckAction(ActionType type) {
        super(type);
        resultPin = addPin(resultPin);
    }

    public CheckAction(JsonObject jsonObject) {
        super(jsonObject);
        resultPin = reAddPin(resultPin);
    }

    public Pin getResultPin() {
        return resultPin;
    }
}
