package top.bogey.touch_tool.data.action.operator;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.state.StateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinArea;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class AreaContainAction extends StateAction {
    protected transient Pin areaPin = new Pin(new PinArea(), R.string.action_area_contain_operator_subtitle_area);
    protected transient Pin posPin = new Pin(new PinPoint(), R.string.action_area_contain_operator_subtitle_position);

    public AreaContainAction() {
        super(R.string.action_area_contain_operator_title);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
    }

    public AreaContainAction(JsonObject jsonObject) {
        super(R.string.action_area_contain_operator_title, jsonObject);
        areaPin = reAddPin(areaPin);
        posPin = reAddPin(posPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        value.setValue(false);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        Rect area = ((PinArea) getPinValue(runnable, actionContext, areaPin)).getArea(service);
        PinPoint point = (PinPoint) getPinValue(runnable, actionContext, posPin);
        value.setValue(area.contains(point.getX(), point.getY()));
    }
}
