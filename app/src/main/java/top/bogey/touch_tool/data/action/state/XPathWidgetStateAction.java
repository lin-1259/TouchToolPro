package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.HashMap;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinXPath;

public class XPathWidgetStateAction extends StateAction {
    private transient Pin xPathPin = new Pin(new PinXPath(), R.string.action_xpath_widget_state_subtitle_xpath);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_state_subtitle_position, PinDirection.OUT);
    private transient Pin nodePin = new Pin(new PinNodeInfo(), R.string.action_state_subtitle_node_info, PinDirection.OUT);
    private final transient Pin valuePin = new Pin(new PinInteger());

    public XPathWidgetStateAction() {
        super(R.string.action_xpath_widget_state_title);
        xPathPin = addPin(xPathPin);
        posPin = addPin(posPin);
        nodePin = addPin(nodePin);
    }

    public XPathWidgetStateAction(JsonObject jsonObject) {
        super(R.string.action_xpath_widget_state_title, jsonObject);
        xPathPin = reAddPin(xPathPin);
        posPin = reAddPin(posPin);
        nodePin = reAddPin(nodePin);
        reAddPin(valuePin, 0);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();

        HashMap<String, Integer> map = new HashMap<>();
        for (Pin showPin : getShowPins()) {
            if (showPin.isRemoveAble()) {
                PinInteger integer = (PinInteger) getPinValue(runnable, actionContext, showPin);
                map.put(showPin.getTitle(null), integer.getValue());
            }
        }

        PinXPath xPath = (PinXPath) getPinValue(runnable, actionContext, xPathPin);
        AccessibilityNodeInfo node = xPath.getPathNode(service.getNeedWindowsRoot(), map);
        if (node != null) {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect bounds = new Rect();
            node.getBoundsInScreen(bounds);
            point.setX(bounds.centerX());
            point.setY(bounds.centerY());

            PinNodeInfo nodePinValue = (PinNodeInfo) nodePin.getValue();
            nodePinValue.setNodeInfo(node);

            return;
        }

        value.setValue(false);
    }

    public Pin getxPathPin() {
        return xPathPin;
    }

    public Pin getNodePin() {
        return nodePin;
    }
}
