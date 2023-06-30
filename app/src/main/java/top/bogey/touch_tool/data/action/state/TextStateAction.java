package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinArea;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class TextStateAction extends StateAction {
    private transient Pin textPin = new Pin(new PinString(), R.string.action_text_state_subtitle_text);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_state_subtitle_position, PinDirection.OUT);
    private transient Pin nodePin = new Pin(new PinNodeInfo(), R.string.action_state_subtitle_node_info, PinDirection.OUT);
    private transient Pin areaPin = new Pin(new PinArea(), R.string.action_state_subtitle_area);

    public TextStateAction() {
        super(R.string.action_text_state_title);
        textPin = addPin(textPin);
        posPin = addPin(posPin);
        nodePin = addPin(nodePin);
        areaPin = addPin(areaPin);
    }

    public TextStateAction(JsonObject jsonObject) {
        super(R.string.action_text_state_title, jsonObject);
        textPin = reAddPin(textPin);
        posPin = reAddPin(posPin);
        nodePin = reAddPin(nodePin);
        areaPin = reAddPin(areaPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (!pin.getId().equals(statePin.getId())) return;
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();

        String text = ((PinString) getPinValue(runnable, actionContext, textPin)).getValue();
        if (text == null || text.isEmpty()) {
            value.setValue(false);
            return;
        }

        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        PinArea area = (PinArea) getPinValue(runnable, actionContext, areaPin);
        AccessibilityNodeInfo searchNode = searchNode(root, Pattern.compile(text), area.getArea(service));
        if (searchNode == null) value.setValue(false);
        else {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect bounds = new Rect();
            searchNode.getBoundsInScreen(bounds);
            point.setX(bounds.centerX());
            point.setY(bounds.centerY());

            PinNodeInfo nodePinValue = (PinNodeInfo) nodePin.getValue();
            nodePinValue.setNodeInfo(searchNode);
        }
    }

    private AccessibilityNodeInfo searchNode(AccessibilityNodeInfo nodeInfo, Pattern pattern, Rect area) {
        if (nodeInfo == null) return null;
        Rect rect = new Rect();
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                child.getBoundsInScreen(rect);
                if (!Rect.intersects(area, rect)) continue;
                CharSequence text = child.getText();
                if (text != null && text.length() > 0) {
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) return child;
                }
                AccessibilityNodeInfo node = searchNode(child, pattern, area);
                if (node != null) return node;
            }
        }
        return null;
    }

    public Pin getTextPin() {
        return textPin;
    }

    public Pin getNodePin() {
        return nodePin;
    }
}
