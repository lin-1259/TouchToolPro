package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class OpenUriAction extends NormalAction {
    private transient Pin uriPin = new Pin(new PinString(), R.string.action_open_url_action_subtitle_url);

    public OpenUriAction() {
        super(ActionType.OPEN_URI);
        uriPin = addPin(uriPin);
    }

    public OpenUriAction(JsonObject jsonObject) {
        super(jsonObject);
        uriPin = reAddPin(uriPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinString uri = (PinString) getPinValue(runnable, context, uriPin);
        AppUtils.gotoScheme(MainApplication.getInstance(), uri.getValue());
        runnable.sleep(100);
        executeNext(runnable, context, outPin);
    }
}
