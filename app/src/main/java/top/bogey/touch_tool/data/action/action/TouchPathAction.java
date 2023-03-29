package top.bogey.touch_tool.data.action.action;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinValueArea;

public class TouchPathAction extends NormalAction {
    private transient Pin pathPin = new Pin(new PinPath(), R.string.action_touch_path_action_subtitle_path);
    private transient Pin timePin = new Pin(new PinValueArea(100, 60000, 100, 300, 300), R.string.action_touch_path_action_subtitle_time);
    private transient Pin offsetPin = new Pin(new PinBoolean(), R.string.action_touch_path_action_subtitle_offset);

    public TouchPathAction() {
        super(R.string.action_touch_path_action_title);
        pathPin = addPin(pathPin);
        timePin = addPin(timePin);
        offsetPin = addPin(offsetPin);
    }

    public TouchPathAction(JsonObject jsonObject) {
        super(R.string.action_touch_path_action_title, jsonObject);
        pathPin = reAddPin(pathPin);
        timePin = reAddPin(timePin);
        offsetPin = reAddPin(offsetPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinPath pinPath = (PinPath) getPinValue(runnable, actionContext, pathPin);
        PinValueArea valueArea = (PinValueArea) getPinValue(runnable, actionContext, timePin);
        PinBoolean offset = (PinBoolean) getPinValue(runnable, actionContext, offsetPin);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        int randomTime = valueArea.getRandomValue();
        service.runGesture(pinPath.getRealPaths(service, offset.getValue()), randomTime, null);
        sleep(randomTime);
        doNextAction(runnable, actionContext, outPin);
    }
}
