package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;
import android.os.Parcel;
import android.view.accessibility.AccessibilityNodeInfo;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinWidget;

public class WidgetStateAction extends StateAction {
    private final Pin<? extends PinObject> widgetPin;
    private final Pin<? extends PinObject> posPin;

    public WidgetStateAction() {
        super();
        widgetPin = addPin(new Pin<>(new PinWidget(), R.string.action_widget_state_subtitle_widget, PinSubType.ID));
        posPin = addPin(new Pin<>(new PinPoint(), R.string.action_state_subtitle_position, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_widget_state_title;
    }

    public WidgetStateAction(Parcel in) {
        super(in);
        widgetPin = addPin(pinsTmp.remove(0));
        posPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_widget_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task, Pin<? extends PinObject> pin) {
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
