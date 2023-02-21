package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class ColorStateAction extends StateAction {
    private transient final Pin colorPin;
    private transient final Pin posPin;

    public ColorStateAction(Context context) {
        super(context, R.string.action_color_state_title);
        colorPin = addPin(new Pin(new PinColor(), context.getString(R.string.action_color_state_subtitle_color)));
        posPin = addPin(new Pin(new PinPoint(), context.getString(R.string.action_state_subtitle_position), PinDirection.OUT, PinSlotType.MULTI));
    }

    public ColorStateAction(JsonObject jsonObject) {
        super(jsonObject);
        colorPin = addPin(tmpPins.remove(0));
        posPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(statePin.getId())) return;

        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getService();
        if (!service.isCaptureEnabled()) {
            value.setValue(false);
            return;
        }

        PinColor color = (PinColor) getPinValue(runnable, actionContext, colorPin);
        if (!color.isValid()) {
            value.setValue(false);
            return;
        }

        List<Rect> rectList = service.binder.matchColor(color.getColor(), color.getArea(service));
        if (rectList == null || rectList.isEmpty()) value.setValue(false);
        else {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect rect = rectList.get(0);
            point.setX(rect.centerX());
            point.setY(rect.centerY());
        }
    }
}
