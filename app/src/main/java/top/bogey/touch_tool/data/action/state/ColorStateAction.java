package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;
import android.os.Parcel;

import java.util.List;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class ColorStateAction extends StateAction {
    private final Pin<? extends PinObject> colorPin;
    private final Pin<? extends PinObject> posPin;

    public ColorStateAction() {
        super();
        colorPin = addPin(new Pin<>(new PinColor(), R.string.action_color_state_subtitle_color));
        posPin = addPin(new Pin<>(new PinPoint(), R.string.action_state_subtitle_postion, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_color_state_title;
    }

    public ColorStateAction(Parcel in) {
        super(in);
        colorPin = addPin(pinsTmp.remove(0));
        posPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_color_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(statePin.getId())) return;
        PinBoolean value = (PinBoolean) getPinValue(worldState, task, statePin);
        MainAccessibilityService service = MainApplication.getService();
        if (!service.isCaptureEnabled()) {
            value.setValue(false);
            return;
        }

        PinColor color = (PinColor) getPinValue(worldState, task, colorPin);
        if (!color.isValid()) {
            value.setValue(false);
            return;
        }

        List<Rect> rects = service.binder.matchColor(color.getColor());
        if (rects == null || rects.isEmpty()) value.setValue(false);
        else {
            value.setValue(true);
            Rect rect = rects.get(0);
            PinPoint position = (PinPoint) getPinValue(worldState, task, posPin);
            position.setX(rect.centerX());
            position.setY(rect.centerY());
        }
    }
}
