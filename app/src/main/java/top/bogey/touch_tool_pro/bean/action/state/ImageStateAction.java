package top.bogey.touch_tool_pro.bean.action.state;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class ImageStateAction extends Action {
    private transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image, true);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);

    public ImageStateAction() {
        super(ActionType.IMAGE_STATE);
        needCapture = true;
        imagePin = addPin(imagePin);
        areaPin = addPin(areaPin);
    }

    public ImageStateAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        imagePin = reAddPin(imagePin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return;

        Bitmap image = service.binder.getCurrImage();
        if (image == null) return;

        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Bitmap bitmap = DisplayUtils.safeCreateBitmap(image, area.getArea(service));
        imagePin.getValue(PinImage.class).setImage(service, bitmap);
    }
}
