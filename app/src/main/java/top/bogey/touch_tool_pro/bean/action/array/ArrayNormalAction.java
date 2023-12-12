package top.bogey.touch_tool_pro.bean.action.array;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;

public class ArrayNormalAction extends ArrayWithAction {
    protected transient Pin inPin = new Pin(new PinExecute(), R.string.pin_execute);
    protected transient Pin outPin = new Pin(new PinExecute(), R.string.pin_execute, true);

    public ArrayNormalAction(ActionType type) {
        super(type);
        inPin = addPin(inPin);
        outPin = addPin(outPin);
    }

    public ArrayNormalAction(JsonObject jsonObject) {
        super(jsonObject);
        inPin = reAddPin(inPin);
        outPin = reAddPin(outPin);
    }
}
