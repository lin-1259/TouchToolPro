package top.bogey.touch_tool_pro.bean.action.image;

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
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class SubImageAction extends Action {
    private transient Pin resultPin = new Pin(new PinImage(), R.string.pin_image, true);
    private transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);

    public SubImageAction() {
        super(ActionType.IMAGE_SUB_IMAGE);
        needCapture = true;
        resultPin = addPin(resultPin);
        imagePin = addPin(imagePin);
        areaPin = addPin(areaPin);
    }

    public SubImageAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        resultPin = reAddPin(resultPin);
        imagePin = reAddPin(imagePin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinImage image = (PinImage) getPinValue(runnable, context, imagePin);
        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        MainApplication instance = MainApplication.getInstance();
        Bitmap bitmap = image.getImage(instance);
        if (bitmap == null) {
            resultPin.getValue(PinImage.class).setImage(null);
        } else {
            Bitmap newBitmap = DisplayUtils.safeCreateBitmap(bitmap, area.getArea(instance));
            resultPin.getValue(PinImage.class).setImage(instance, newBitmap);
        }
    }
}
