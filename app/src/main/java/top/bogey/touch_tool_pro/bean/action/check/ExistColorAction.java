package top.bogey.touch_tool_pro.bean.action.check;

import android.graphics.Rect;

import com.google.gson.JsonObject;

import java.util.List;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class ExistColorAction extends CheckAction {
    private transient Pin colorPin = new Pin(new PinColor(), R.string.pin_color);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);


    public ExistColorAction() {
        super(ActionType.CHECK_EXIST_COLOR);
        colorPin = addPin(colorPin);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
    }

    public ExistColorAction(JsonObject jsonObject) {
        super(jsonObject);
        colorPin = reAddPin(colorPin);
        areaPin = reAddPin(areaPin);
        posPin = reAddPin(posPin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!pin.equals(resultPin)) return;

        PinBoolean result = resultPin.getValue(PinBoolean.class);
        result.setBool(false);

        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (!service.isCaptureEnabled()) return;

        PinColor color = (PinColor) getPinValue(runnable, context, colorPin);
        if (color.getColor() == null) return;

        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        List<Rect> rectList = service.binder.matchColor(color.getColor(), area.getArea(service));
        if (rectList == null || rectList.isEmpty()) return;

        result.setBool(true);
        Rect rect = rectList.get(0);
        posPin.getValue(PinPoint.class).setPoint(service, rect.centerX(), rect.centerY());
    }

    public Pin getColorPin() {
        return colorPin;
    }

    public Pin getPosPin() {
        return posPin;
    }
}
