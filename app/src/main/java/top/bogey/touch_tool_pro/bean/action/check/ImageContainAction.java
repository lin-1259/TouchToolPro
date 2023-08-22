package top.bogey.touch_tool_pro.bean.action.check;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.MatchResult;

public class ImageContainAction extends CheckAction {
    private transient Pin imagePin = new Pin(new PinImage(), R.string.pin_image);
    private transient Pin otherPin = new Pin(new PinImage(), R.string.action_image_check_subtitle_other);
    private transient Pin similarPin = new Pin(new PinInteger(85), R.string.action_exist_image_check_subtitle_similar);
    private transient Pin colorPin = new Pin(new PinBoolean(), R.string.action_exist_image_check_subtitle_with_color);

    public ImageContainAction() {
        super(ActionType.CHECK_IMAGE);
        imagePin = addPin(imagePin);
        otherPin = addPin(otherPin);
        similarPin = addPin(similarPin);
        colorPin = addPin(colorPin);
    }

    public ImageContainAction(JsonObject jsonObject) {
        super(jsonObject);
        imagePin = reAddPin(imagePin);
        otherPin = reAddPin(otherPin);
        similarPin = reAddPin(similarPin);
        colorPin = reAddPin(colorPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);
        result.setBool(false);

        MainApplication instance = MainApplication.getInstance();
        PinImage image = (PinImage) getPinValue(runnable, context, imagePin);
        Bitmap bitmap = image.getImage(instance);
        if (bitmap == null) return;
        PinImage other = (PinImage) getPinValue(runnable, context, otherPin);
        Bitmap otherBitmap = other.getImage(instance);
        if (otherBitmap == null) return;

        PinInteger similar = (PinInteger) getPinValue(runnable, context, similarPin);
        PinBoolean withColor = (PinBoolean) getPinValue(runnable, context, colorPin);
        MatchResult matchResult = AppUtils.nativeMatchTemplate(bitmap, otherBitmap, withColor.isBool());
        if (matchResult.value >= similar.getValue()) result.setBool(true);
    }
}
