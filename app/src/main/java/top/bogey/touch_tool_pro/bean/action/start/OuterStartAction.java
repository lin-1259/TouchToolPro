package top.bogey.touch_tool_pro.bean.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class OuterStartAction extends StartAction {
    private transient Pin urlPin = new Pin(new PinString(PinSubType.URL), R.string.action_outer_start_subtitle_url);
    private transient Pin shortcutPin = new Pin(new PinString(PinSubType.SHORTCUT), R.string.action_outer_start_subtitle_shortcut);

    public OuterStartAction() {
        super(ActionType.OUTER_START);
        urlPin = addPin(urlPin);
        shortcutPin = addPin(shortcutPin);
        setId(getId());
    }

    public OuterStartAction(JsonObject jsonObject) {
        super(jsonObject);
        urlPin = reAddPin(urlPin);
        shortcutPin = reAddPin(shortcutPin);
        setId(getId());
    }

    @Override
    public void setId(String id) {
        super.setId(id);
        urlPin.getValue(PinString.class).setValue(getId());
        shortcutPin.getValue(PinString.class).setValue(getId());
    }

    @Override
    public boolean checkReady(TaskRunnable runnable, FunctionContext context) {
        return true;
    }
}
