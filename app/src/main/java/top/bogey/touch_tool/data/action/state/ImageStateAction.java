package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class ImageStateAction extends StateAction {
    private final Pin<? extends PinObject> imagePin;
    private final Pin<? extends PinObject> similarPin;
    private final Pin<? extends PinObject> posPin;

    public ImageStateAction(Context context) {
        super(context, R.string.action_image_state_title);
        imagePin = addPin(new Pin<>(new PinImage(), context.getString(R.string.action_image_state_subtitle_image)));
        similarPin = addPin(new Pin<>(new PinInteger(85), context.getString(R.string.action_image_state_subtitle_similar)));
        posPin = addPin(new Pin<>(new PinPoint(), context.getString(R.string.action_state_subtitle_position), PinDirection.OUT, PinSlotType.MULTI));
    }

    public ImageStateAction(Parcel in) {
        super(in);
        imagePin = addPin(pinsTmp.remove(0));
        similarPin = addPin(pinsTmp.remove(0));
        posPin = addPin(pinsTmp.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
        if (!pin.getId().equals(statePin.getId())) return;
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getService();
        if (!service.isCaptureEnabled()) {
            value.setValue(false);
            return;
        }

        PinImage image = (PinImage) getPinValue(worldState, task, imagePin);
        if (image.getBitmap() == null) {
            value.setValue(false);
            return;
        }

        PinInteger similar = (PinInteger) getPinValue(worldState, task, similarPin);
        Rect rect = service.binder.matchImage(image.getScaleBitmap(service), similar.getValue(), image.getArea(service));
        if (rect == null) {
            value.setValue(false);
        } else {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            point.setX(rect.centerX());
            point.setY(rect.centerY());
        }
    }
}
