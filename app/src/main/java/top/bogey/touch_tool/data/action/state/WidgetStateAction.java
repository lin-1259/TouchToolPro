package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.utils.DisplayUtils;

public class WidgetStateAction extends StateAction {
    private transient Pin widgetPin = new Pin(new PinWidget(), R.string.action_widget_state_subtitle_widget);
    private transient Pin posPin = new Pin(new PinPoint(), R.string.action_state_subtitle_position, PinDirection.OUT);
    private transient Pin nodePin = new Pin(new PinNodeInfo(), R.string.action_state_subtitle_node_info, PinDirection.OUT);

    public WidgetStateAction() {
        super(R.string.action_widget_state_title);
        widgetPin = addPin(widgetPin);
        posPin = addPin(posPin);
        nodePin = addPin(nodePin);
    }

    public WidgetStateAction(JsonObject jsonObject) {
        super(R.string.action_widget_state_title, jsonObject);
        widgetPin = reAddPin(widgetPin);
        posPin = reAddPin(posPin);
        if (pinsTmp.size() != 0) {
            nodePin = reAddPin(nodePin);
        } else nodePin = null;
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getInstance().getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();

        PinWidget widget = (PinWidget) getPinValue(runnable, actionContext, widgetPin);
        AccessibilityNodeInfo node = widget.getNode(DisplayUtils.getScreenArea(service), root);
        if (node != null) {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect bounds = new Rect();
            node.getBoundsInScreen(bounds);
            point.setX(bounds.centerX());
            point.setY(bounds.centerY());

            if (nodePin != null) {
                PinNodeInfo nodePinValue = (PinNodeInfo) nodePin.getValue();
                nodePinValue.setNodeInfo(node);
            }

            return;
        }

        value.setValue(false);
    }
}
