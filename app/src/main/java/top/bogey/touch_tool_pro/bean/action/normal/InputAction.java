package top.bogey.touch_tool_pro.bean.action.normal;

import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;

public class InputAction extends NormalAction {
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, true);
    private transient Pin nodePin = new Pin(new PinNode(), R.string.pin_node);
    private transient Pin textPin = new Pin(new PinString(), R.string.action_input_node_action_subtitle_content);
    private transient Pin appendPin = new Pin(new PinBoolean(false), R.string.action_input_node_action_subtitle_append);
    private transient Pin enterPin = new Pin(new PinBoolean(false), R.string.action_input_node_action_subtitle_enter);

    public InputAction() {
        super(ActionType.INPUT);
        falsePin = addPin(falsePin);
        nodePin = addPin(nodePin);
        textPin = addPin(textPin);
        appendPin = addPin(appendPin);
        enterPin = addPin(enterPin);
    }

    public InputAction(JsonObject jsonObject) {
        super(jsonObject);
        falsePin = reAddPin(falsePin);
        nodePin = reAddPin(nodePin);
        textPin = reAddPin(textPin);
        appendPin = reAddPin(appendPin);
        enterPin = reAddPin(enterPin);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        PinNode node = (PinNode) getPinValue(runnable, context, nodePin);
        boolean result = false;
        if (node.getNode() != null && node.getNode().isEditable()) {
            PinString text = (PinString) getPinValue(runnable, context, textPin);
            String value = text.getValue();
            if (value != null && !value.isEmpty()) {
                PinBoolean append = (PinBoolean) getPinValue(runnable, context, appendPin);
                if (append.isBool()) {
                    CharSequence nodeText = node.getNode().getText();
                    if (nodeText != null) value = nodeText + value;
                }
                Bundle bundle = new Bundle();
                bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value);
                result = node.getNode().performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);

                PinBoolean enter = (PinBoolean) getPinValue(runnable, context, enterPin);
                if (result && enter.isBool() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    node.getNode().performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER.getId());
                }
            }
        }
        if (result) executeNext(runnable, context, outPin);
        else executeNext(runnable, context, falsePin);
    }
}
