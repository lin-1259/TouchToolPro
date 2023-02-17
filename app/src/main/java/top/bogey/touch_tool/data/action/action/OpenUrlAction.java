package top.bogey.touch_tool.data.action.action;

import android.content.Context;

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
    private transient final Pin urlPin;

    public OpenUrlAction(Context context) {
        super(context, R.string.action_open_url_action_title);
        urlPin = addPin(new Pin(new PinString(), context.getString(R.string.action_open_url_action_subtitle_url)));
    }

    public OpenUrlAction(JsonObject jsonObject) {
        super(jsonObject);
        urlPin = addPin(tmpPins.remove(0));
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinString pinString = (PinString) getPinValue(actionContext, urlPin);

        MainAccessibilityService service = MainApplication.getService();
        AppUtils.gotoUrl(service, pinString.getValue());

        doNextAction(runnable, actionContext, outPin);
    }
}
