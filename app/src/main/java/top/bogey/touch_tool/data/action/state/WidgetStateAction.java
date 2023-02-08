package top.bogey.touch_tool.data.action.state;

import android.content.Context;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.action.StateAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinWidget;

public class WidgetStateAction extends StateAction {
    private transient final Pin<?> widgetPin;
    private transient final Pin<?> posPin;

    public WidgetStateAction(Context context) {
        super(context, R.string.action_widget_state_title);
        widgetPin = addPin(new Pin<>(new PinWidget(), context.getString(R.string.action_widget_state_subtitle_widget), PinSubType.ID));
        posPin = addPin(new Pin<>(new PinPoint(), context.getString(R.string.action_state_subtitle_position), PinDirection.OUT, PinSlotType.MULTI));
    }

    public WidgetStateAction(JsonObject jsonObject) {
        super(jsonObject);
        widgetPin = addPin(tmpPins.remove(0));
        posPin = addPin(tmpPins.remove(0));
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<?> pin) {
        PinBoolean value = (PinBoolean) statePin.getValue();
        MainAccessibilityService service = MainApplication.getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();

        PinWidget widget = (PinWidget) getPinValue(worldState, task, widgetPin);
        AccessibilityNodeInfo node = widget.getNode(root);
        if (node != null) {
            value.setValue(true);
            PinPoint point = (PinPoint) posPin.getValue();
            Rect bounds = new Rect();
            node.getBoundsInScreen(bounds);
            point.setX(bounds.centerX());
            point.setY(bounds.centerY());
            return;
        }

        value.setValue(false);
    }
}
