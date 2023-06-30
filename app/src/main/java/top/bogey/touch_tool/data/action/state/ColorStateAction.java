package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.data.pin.object.PinArea;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class ColorStateAction extends StateAction {
    private transient Pin colorPin = new Pin(new PinColor(), R.string.action_color_state_subtitle_color);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_state_subtitle_position, PinDirection.OUT);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.action_state_subtitle_area);

    public ColorStateAction() {
        super(R.string.action_color_state_title);
        colorPin = addPin(colorPin);
        posPin = addPin(posPin);
        areaPin = addPin(areaPin);
    }

    public ColorStateAction(JsonObject jsonObject) {
        super(R.string.action_color_state_title, jsonObject);
        colorPin = reAddPin(colorPin);
        posPin = reAddPin(posPin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(statePin.getId())) return;

        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) {
            value.setValue(false);
            return;
        }

        PinColor color = (PinColor) getPinValue(runnable, actionContext, colorPin);
        if (!color.isEmpty()) {
            value.setValue(false);
            return;
        }

        PinArea area = (PinArea) getPinValue(runnable, actionContext, areaPin);
        List<Rect> rectList = service.binder.matchColor(color.getColor(), area.getArea(service));
        if (rectList == null || rectList.isEmpty()) value.setValue(false);
        else {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect rect = rectList.get(0);
            point.setX(rect.centerX());
            point.setY(rect.centerY());
        }
    }

    public Pin getColorPin() {
        return colorPin;
    }

    public Pin getPosPin() {
        return posPin;
    }
}
