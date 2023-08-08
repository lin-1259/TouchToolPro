package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class NormalStartAction extends StartAction {
    private transient Pin startPin = new Pin(new PinBoolean(false), R.string.action_normal_start_subtitle_condition);

    public NormalStartAction() {
        super(ActionType.NORMAL_START);
        startPin = addPin(startPin);
    }

    public NormalStartAction(JsonObject jsonObject) {
        super(jsonObject);
        startPin = reAddPin(startPin);
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        PinBoolean value = (PinBoolean) getPinValue(runnable, context, startPin);
        return value.isBool();
    }
}
