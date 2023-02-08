package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinString;

public class TextStateAction extends StateAction {
    private transient final Pin<?> textPin;
    private transient final Pin<?> posPin;

    public TextStateAction(Context context) {
        super(context, R.string.action_text_state_title);
        textPin = addPin(new Pin<>(new PinString(), context.getString(R.string.action_text_state_subtitle_text)));
        posPin = addPin(new Pin<>(new PinPoint(), context.getString(R.string.action_state_subtitle_position), PinDirection.OUT, PinSlotType.MULTI));
    }

    public TextStateAction(JsonObject jsonObject) {
        super(jsonObject);
        textPin = addPin(tmpPins.remove(0));
        posPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<?> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getService();

        String text = ((PinString) getPinValue(worldState, task, textPin)).getValue();
        if (text == null || text.isEmpty()) {
            value.setValue(false);
            return;
        }

        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        AccessibilityNodeInfo searchNode = searchNode(root, Pattern.compile(text));
        if (searchNode == null) value.setValue(false);
        else {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect bounds = new Rect();
            searchNode.getBoundsInScreen(bounds);
            point.setX(bounds.centerX());
            point.setY(bounds.centerY());
        }
    }

    private AccessibilityNodeInfo searchNode(AccessibilityNodeInfo nodeInfo, Pattern pattern) {
        if (nodeInfo == null) return null;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                CharSequence text = child.getText();
                if (text != null && text.length() > 0) {
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) return child;
                }
                AccessibilityNodeInfo node = searchNode(child, pattern);
                if (node != null) return node;
            }
        }
        return null;
    }
}
