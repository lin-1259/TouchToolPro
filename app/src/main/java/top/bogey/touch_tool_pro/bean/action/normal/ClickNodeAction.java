package top.bogey.touch_tool_pro.bean.action.normal;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class ClickNodeAction extends NormalAction {
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);
    private transient Pin longTouchPin = new Pin(new PinBoolean(false), R.string.action_touch_node_action_subtitle_long_touch);

    public ClickNodeAction() {
        super(ActionType.CLICK_NODE);
        falsePin = addPin(falsePin);
        nodePin = addPin(nodePin);
        longTouchPin = addPin(longTouchPin);
    }

    public ClickNodeAction(JsonObject jsonObject) {
        super(jsonObject);
        falsePin = reAddPin(falsePin);
        nodePin = reAddPin(nodePin);
        longTouchPin = reAddPin(longTouchPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinNode node = (PinNode) getPinValue(runnable, context, nodePin);
        AccessibilityNodeInfo parent = getClickAbleParent(node.getNode());
        if (parent != null) {
            PinBoolean longTouch = (PinBoolean) getPinValue(runnable, context, longTouchPin);
            if (longTouch.isBool()) {
                if (parent.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK)) {
                    runnable.sleep(500);
                    executeNext(runnable, context, outPin);
                    return;
                }
            } else {
                if (parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    runnable.sleep(100);
                    executeNext(runnable, context, outPin);
                    return;
                }
            }
        }
        executeNext(runnable, context, falsePin);
    }

    private AccessibilityNodeInfo getClickAbleParent(AccessibilityNodeInfo node) {
        if (node == null) return null;
        if (node.isVisibleToUser()) {
            if (node.isClickable() || node.isEditable() || node.isCheckable() || node.isLongClickable()) return node;
        }
        return getClickAbleParent(node.getParent());
    }

    public Pin getNodePin() {
        return nodePin;
    }
}
