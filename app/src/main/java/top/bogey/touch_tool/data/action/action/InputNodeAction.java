package top.bogey.touch_tool.data.action.action;

import android.os.Build;
import android.os.Bundle;
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
import top.bogey.touch_tool.data.pin.object.PinString;

public class InputNodeAction extends NormalAction {
    private transient Pin nodePin = new Pin(new PinNodeInfo(), R.string.action_state_subtitle_node_info);
    private transient Pin contentPin = new Pin(new PinString(), R.string.action_input_node_action_subtitle_content);
    private transient Pin appendPin = new Pin(new PinBoolean(false), R.string.action_input_node_action_subtitle_append);
    private transient Pin falsePin = new Pin(new PinExecute(), R.string.action_logic_subtitle_false, PinDirection.OUT);
    private transient Pin enterPin = new Pin(new PinBoolean(false), R.string.action_input_node_action_subtitle_enter);

    public InputNodeAction() {
        super(R.string.action_input_node_action_title);
        nodePin = addPin(nodePin);
        contentPin = addPin(contentPin);
        appendPin = addPin(appendPin);
        falsePin = addPin(falsePin);
        enterPin = addPin(enterPin);
    }

    public InputNodeAction(JsonObject jsonObject) {
        super(R.string.action_input_node_action_title, jsonObject);
        nodePin = reAddPin(nodePin);
        contentPin = reAddPin(contentPin);
        appendPin = reAddPin(appendPin);
        falsePin = reAddPin(falsePin);
        enterPin = reAddPin(enterPin);
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinNodeInfo pinNodeInfo = (PinNodeInfo) getPinValue(runnable, actionContext, nodePin);
        AccessibilityNodeInfo nodeInfo = pinNodeInfo.getNodeInfo();
        boolean result;
        if (nodeInfo == null || !nodeInfo.isEditable()) {
            result = false;
        } else {
            PinString content = (PinString) getPinValue(runnable, actionContext, contentPin);
            String text = content.getValue();
            PinBoolean append = (PinBoolean) getPinValue(runnable, actionContext, appendPin);
            if (append.getValue() && nodeInfo.getText() != null) {
                if (text == null) text = nodeInfo.getText().toString();
                else text = nodeInfo.getText().toString() + text;
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);

            boolean enter = false;
            if (enterPin != null) enter = ((PinBoolean) getPinValue(runnable, actionContext, enterPin)).getValue();
            if (result && enter && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                nodeInfo.performAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER.getId());
            }
        }
        sleep(100);
        if (result) {
            doNextAction(runnable, actionContext, outPin);
        } else {
            doNextAction(runnable, actionContext, falsePin);
        }
    }
}
