package top.bogey.touch_tool.data.action.action;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.utils.AppUtils;

public class OpenUrlAction extends NormalAction {
    private transient Pin urlPin = new Pin(new PinString(), R.string.action_open_url_action_subtitle_url);

    public OpenUrlAction() {
        super(R.string.action_open_url_action_title);
        urlPin = addPin(urlPin);
    }

    public OpenUrlAction(JsonObject jsonObject) {
        super(R.string.action_open_url_action_title, jsonObject);
        urlPin = reAddPin(urlPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinString pinString = (PinString) getPinValue(runnable, actionContext, urlPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        AppUtils.gotoScheme(service, pinString.getValue());

        doNextAction(runnable, actionContext, outPin);
    }
}
