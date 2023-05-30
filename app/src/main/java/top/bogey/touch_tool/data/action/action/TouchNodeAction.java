package top.bogey.touch_tool.data.action.action;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;

public class TouchNodeAction extends NormalAction {
    private transient Pin nodePin = new Pin(new PinNodeInfo(), R.string.action_state_subtitle_node_info);
    private transient Pin longTouchPin = new Pin(new PinBoolean(false), R.string.action_touch_node_action_subtitle_long_touch);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, PinDirection.OUT);

    public TouchNodeAction() {
        super(R.string.action_touch_node_action_title);
        nodePin = addPin(nodePin);
        longTouchPin = addPin(longTouchPin);
        falsePin = addPin(falsePin);
    }

    public TouchNodeAction(JsonObject jsonObject) {
        super(R.string.action_touch_node_action_title, jsonObject);
        nodePin = reAddPin(nodePin);
        longTouchPin = reAddPin(longTouchPin);
        falsePin = reAddPin(falsePin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinNodeInfo pinNodeInfo = (PinNodeInfo) getPinValue(runnable, actionContext, nodePin);
        PinBoolean longTouch = (PinBoolean) getPinValue(runnable, actionContext, longTouchPin);
        AccessibilityNodeInfo nodeInfo = pinNodeInfo.getNodeInfo();
        boolean result = false;
        if (nodeInfo != null) {
            AccessibilityNodeInfo clickAble = getClickAbleParent(nodeInfo);
            if (clickAble != null) {
                result = clickAble.performAction(longTouch.getValue() ? AccessibilityNodeInfo.ACTION_LONG_CLICK : AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        if (result) {
            sleep(longTouch.getValue() ? 500 : 100);
            doNextAction(runnable, actionContext, outPin);
        } else {
            doNextAction(runnable, actionContext, falsePin);
        }
    }

    public static AccessibilityNodeInfo getClickAbleParent(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return null;
        if (nodeInfo.isClickable() || nodeInfo.isEditable() || nodeInfo.isCheckable() || nodeInfo.isLongClickable()) return nodeInfo;
        return getClickAbleParent(nodeInfo.getParent());
    }

    public Pin getNodePin() {
        return nodePin;
    }
}
