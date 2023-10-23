package top.bogey.touch_tool_pro.bean.action.check;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.ocr.OcrResult;
import top.bogey.touch_tool_pro.utils.ocr.Predictor;

public class ExistTextOcrAction extends CheckAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.pin_string);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.pin_area);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.pin_point, true);

    public ExistTextOcrAction() {
        super(ActionType.CHECK_EXIST_TEXT_OCR);
        needCapture = true;
        textPin = addPin(textPin);
        areaPin = addPin(areaPin);
        posPin = addPin(posPin);
    }

    public ExistTextOcrAction(JsonObject jsonObject) {
        super(jsonObject);
        needCapture = true;
        textPin = reAddPin(textPin);
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

        PinString text = (PinString) getPinValue(runnable, context, textPin);
        if (text.getValue() == null || text.getValue().isEmpty()) return;

        PinArea area = (PinArea) getPinValue(runnable, context, areaPin);
        Rect areaArea = area.getArea(service);
        Bitmap currImage = service.binder.getCurrImage();
        Bitmap bitmap = DisplayUtils.safeCreateBitmap(currImage, areaArea);
        ArrayList<OcrResult> results = Predictor.getInstance().runOcr(bitmap);

        Pattern pattern = Pattern.compile(text.getValue());
        for (OcrResult ocrResult : results) {
            if (pattern.matcher(ocrResult.getLabel()).find()) {
                result.setBool(true);
                Rect rect = ocrResult.getArea();
                rect.offset(areaArea.left, areaArea.top);
                PinPoint value = posPin.getValue(PinPoint.class);
                value.setPoint(service, rect.centerX(), rect.centerY());
                break;
            }
        }
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        if (resultPin.getLinks().isEmpty()) {
            if (!posPin.getLinks().isEmpty()) {
                return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_exist_action_tips);
            }
        }
        return super.check(context);
    }
}
