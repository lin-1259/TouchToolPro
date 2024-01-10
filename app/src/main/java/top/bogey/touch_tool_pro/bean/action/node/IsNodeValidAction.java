package top.bogey.touch_tool_pro.bean.action.node;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.other.CheckAction;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class IsNodeValidAction extends CheckAction {
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);

    public IsNodeValidAction() {
        super(ActionType.NODE_IS_VALID);
        nodePin = addPin(nodePin);
    }

    public IsNodeValidAction(JsonObject jsonObject) {
        super(jsonObject);
        nodePin = reAddPin(nodePin);
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinNode node = (PinNode) getPinValue(runnable, context, nodePin);
        AccessibilityNodeInfo nodeInfo = node.getNode();
        resultPin.getValue(PinBoolean.class).setBool(nodeInfo != null);
    }
}
