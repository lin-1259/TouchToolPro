package top.bogey.touch_tool.data.action.state;

import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;
import top.bogey.touch_tool.data.pin.object.PinString;

public class WidgetTextStateAction extends StateAction {
    private transient Pin nodePin = new Pin(new PinNodeInfo(), R.string.action_state_subtitle_node_info);
    private transient Pin textPin = new Pin(new PinString(), R.string.action_text_state_subtitle_text, PinDirection.OUT);

    public WidgetTextStateAction() {
        super(R.string.action_widget_text_state_title);
        nodePin = addPin(nodePin);
        textPin = addPin(textPin);
    }

    public WidgetTextStateAction(JsonObject jsonObject) {
        super(R.string.action_widget_text_state_title, jsonObject);
        nodePin = reAddPin(nodePin);
        textPin = reAddPin(textPin);
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();

        PinNodeInfo pinNodeInfo = (PinNodeInfo) getPinValue(runnable, actionContext, nodePin);
        AccessibilityNodeInfo nodeInfo = pinNodeInfo.getNodeInfo();
        if (nodeInfo == null || nodeInfo.getText() == null) {
            value.setValue(false);
            return;
        }

        value.setValue(true);
        PinString pinString = (PinString) textPin.getValue();
        pinString.setValue(nodeInfo.getText().toString());
    }
}
