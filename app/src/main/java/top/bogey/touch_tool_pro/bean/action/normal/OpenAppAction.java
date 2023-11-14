package top.bogey.touch_tool_pro.bean.action.normal;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.PinSubType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class OpenAppAction extends NormalAction {
    private transient Pin appPin = new Pin(new PinApplication(PinSubType.SINGLE_ACTIVITY), R.string.pin_app);

    public OpenAppAction() {
        super(ActionType.OPEN_APP);
        appPin = addPin(appPin);
    }

    public OpenAppAction(JsonObject jsonObject) {
        super(jsonObject);
        appPin = reAddPin(appPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinApplication app = (PinApplication) getPinValue(runnable, context, appPin);
        for (Map.Entry<String, ArrayList<String>> entry : app.getApps().entrySet()) {
            ArrayList<String> activities = entry.getValue();
            if (activities == null || activities.isEmpty()) AppUtils.gotoApp(MainApplication.getInstance(), entry.getKey());
            else AppUtils.gotoActivity(MainApplication.getInstance(), entry.getKey(), activities.get(0));
            break;
        }
        runnable.sleep(100);
        executeNext(runnable, context, outPin);
    }
}
