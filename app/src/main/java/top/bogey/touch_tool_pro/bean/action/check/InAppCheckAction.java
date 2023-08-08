package top.bogey.touch_tool_pro.bean.action.check;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class InAppCheckAction extends CheckAction {
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.MULTI_ACTIVITY), R.string.pin_app);
    private transient Pin checkAppPin = new Pin(new PinApplication(PinSubType.SINGLE_ACTIVITY), R.string.action_in_app_check_subtitle_who);

    public InAppCheckAction() {
        super(ActionType.CHECK_IN_APP);
        appPin = addPin(appPin);
        checkAppPin = addPin(checkAppPin);
    }

    public InAppCheckAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
        checkAppPin = reAddPin(checkAppPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinBoolean result = resultPin.getValue(PinBoolean.class);

        PinApplication checkApp = (PinApplication) getPinValue(runnable, context, checkAppPin);
        PinApplication app = (PinApplication) getPinValue(runnable, context, appPin);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        result.setBool(app.contain(service, checkApp));
    }
}
