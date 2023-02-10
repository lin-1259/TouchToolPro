package top.bogey.touch_tool.data.action.action;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;

public class TouchNodeAction extends NormalAction {
    private transient final Pin nodePin;
    private transient final Pin longTouchPin;
    private transient final Pin falsePin;

    public TouchNodeAction(Context context) {
        super(context, R.string.action_touch_node_action_title);
        nodePin = addPin(new Pin(new PinNodeInfo(), context.getString(R.string.action_state_subtitle_node_info)));
        longTouchPin = addPin(new Pin(new PinBoolean(false), context.getString(R.string.action_touch_node_action_subtitle_long_touch)));
        falsePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_logic_subtitle_false), PinDirection.OUT));
    }

    public TouchNodeAction(JsonObject jsonObject) {
        super(jsonObject);
        nodePin = addPin(tmpPins.remove(0));
        longTouchPin = addPin(tmpPins.remove(0));
        if (tmpPins.size() > 0) {
            falsePin = addPin(tmpPins.remove(0));
        } else falsePin = null;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin pin) {
        PinNodeInfo pinNodeInfo = (PinNodeInfo) getPinValue(worldState, runnable.getTask(), nodePin);
        AccessibilityNodeInfo nodeInfo = pinNodeInfo.getNodeInfo();
        boolean result = false;
        if (nodeInfo != null) {
            AccessibilityNodeInfo clickAble = getClickAbleParent(nodeInfo);
            if (clickAble != null) {
                PinBoolean longTouch = (PinBoolean) getPinValue(worldState, runnable.getTask(), longTouchPin);
                result = clickAble.performAction(longTouch.getValue() ? AccessibilityNodeInfo.ACTION_LONG_CLICK : AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        sleep(100);
        if (result) {
            super.doAction(worldState, runnable, outPin);
        } else {
            super.doAction(worldState, runnable, falsePin);
        }
    }

    private AccessibilityNodeInfo getClickAbleParent(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return null;
        if (nodeInfo.isClickable() || nodeInfo.isEditable() || nodeInfo.isCheckable() || nodeInfo.isLongClickable()) return nodeInfo;
        return getClickAbleParent(nodeInfo.getParent());
    }
}
