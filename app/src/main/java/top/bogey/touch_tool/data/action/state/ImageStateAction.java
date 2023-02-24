package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.graphics.Rect;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinPoint;

public class ImageStateAction extends StateAction {
    private transient final Pin imagePin;
    private transient final Pin similarPin;
    private transient final Pin posPin;

    public ImageStateAction(Context context) {
        super(context, R.string.action_image_state_title);
        imagePin = addPin(new Pin(new PinImage(), context.getString(R.string.action_image_state_subtitle_image)));
        similarPin = addPin(new Pin(new PinInteger(85), context.getString(R.string.action_image_state_subtitle_similar)));
        posPin = addPin(new Pin(new PinPoint(), context.getString(R.string.action_state_subtitle_position), PinDirection.OUT, PinSlotType.MULTI));
    }

    public ImageStateAction(JsonObject jsonObject) {
        super(jsonObject);
        imagePin = addPin(tmpPins.remove(0));
        similarPin = addPin(tmpPins.remove(0));
        posPin = addPin(tmpPins.remove(0));
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

        PinImage image = (PinImage) getPinValue(runnable, actionContext, imagePin);
        if (image.getBitmap() == null) {
            value.setValue(false);
            return;
        }

        PinInteger similar = (PinInteger) getPinValue(runnable, actionContext, similarPin);
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
