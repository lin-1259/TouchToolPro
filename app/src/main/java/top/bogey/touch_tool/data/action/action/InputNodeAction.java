package top.bogey.touch_tool.data.action.action;

import android.content.Context;
import android.os.Bundle;
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
import top.bogey.touch_tool.data.pin.object.PinString;

public class InputNodeAction extends NormalAction {
    private transient final Pin nodePin;
    private transient final Pin contentPin;
    private transient final Pin appendPin;
    private transient final Pin falsePin;

    public InputNodeAction(Context context) {
        super(context, R.string.action_input_node_action_title);
        nodePin = addPin(new Pin(new PinNodeInfo(), context.getString(R.string.action_state_subtitle_node_info)));
        contentPin = addPin(new Pin(new PinString(), context.getString(R.string.action_input_node_action_subtitle_content)));
        appendPin = addPin(new Pin(new PinBoolean(false), context.getString(R.string.action_input_node_action_subtitle_append)));
        falsePin = addPin(new Pin(new PinExecute(), context.getString(R.string.action_logic_subtitle_false), PinDirection.OUT));
    }

    public InputNodeAction(JsonObject jsonObject) {
        super(jsonObject);
        nodePin = addPin(tmpPins.remove(0));
        contentPin = addPin(tmpPins.remove(0));
        appendPin = addPin(tmpPins.remove(0));
        if (tmpPins.size() > 0) {
            falsePin = addPin(tmpPins.remove(0));
        } else falsePin = null;
    }

    @Override
    protected void doAction(WorldState worldState, TaskRunnable runnable, Pin pin) {
        PinNodeInfo pinNodeInfo = (PinNodeInfo) getPinValue(worldState, runnable.getTask(), nodePin);
        AccessibilityNodeInfo nodeInfo = pinNodeInfo.getNodeInfo();
        boolean result;
        if (nodeInfo == null || !nodeInfo.isEditable()) {
            result = false;
        } else {
            PinString content = (PinString) getPinValue(worldState, runnable.getTask(), contentPin);
            String text = content.getValue();
            PinBoolean append = (PinBoolean) getPinValue(worldState, runnable.getTask(), appendPin);
            if (append.getValue() && nodeInfo.getText() != null) {
                if (text == null) text = nodeInfo.getText().toString();
                else text = nodeInfo.getText().toString() + text;
            }
            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
        }
        sleep(100);
        if (result) {
            super.doAction(worldState, runnable, outPin);
        } else {
            super.doAction(worldState, runnable, falsePin);
        }
    }
}
