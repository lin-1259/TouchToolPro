package top.bogey.touch_tool_pro.bean.action.state;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class ColorStateAction extends Action {
    private transient Pin colorPin = new Pin(new PinColor(), R.string.pin_color, true);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point);
    private transient Pin areaPin = new Pin(new PinValueArea(), R.string.pin_value_area);

    public ColorStateAction() {
        super(ActionType.COLOR_STATE);
        colorPin = addPin(colorPin);
        posPin = addPin(posPin);
        areaPin = addPin(areaPin);
    }

    public ColorStateAction(JsonObject jsonObject) {
        super(jsonObject);
        colorPin = reAddPin(colorPin);
        posPin = reAddPin(posPin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return;

        Bitmap image = service.binder.getCurrImage();
        if (image == null) return;

        PinPoint pos = (PinPoint) getPinValue(runnable, context, posPin);
        PinValueArea area = (PinValueArea) getPinValue(runnable, context, areaPin);
        int[] color = DisplayUtils.getHsvColor(image, pos.getX(service), pos.getY(service));

        colorPin.getValue(PinColor.class).setColor(color);
        colorPin.getValue(PinColor.class).setArea(service, area.getLow(), area.getHigh());
    }
}
