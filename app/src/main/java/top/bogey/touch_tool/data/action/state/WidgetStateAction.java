package top.bogey.touch_tool.data.action.state;

import android.graphics.Rect;
import android.os.Parcel;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

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
import top.bogey.touch_tool.data.pin.object.PinString;

public class WidgetStateAction extends StateAction {
    private final Pin<? extends PinObject> idPin;
    private final Pin<? extends PinObject> levelPin;
    private final Pin<? extends PinObject> posPin;

    public WidgetStateAction() {
        super();
        idPin = addPin(new Pin<>(new PinString(), R.string.action_widget_state_subtitle_id, PinSubType.ID));
        levelPin = addPin(new Pin<>(new PinString(), R.string.action_widget_state_subtitle_level, PinSubType.LEVEL));
        posPin = addPin(new Pin<>(new PinPoint(), R.string.action_state_subtitle_postion, PinDirection.OUT, PinSlotType.MULTI));
        titleId = R.string.action_widget_state_title;
    }

    public WidgetStateAction(Parcel in) {
        super(in);
        idPin = addPin(pinsTmp.remove(0));
        levelPin = addPin(pinsTmp.remove(0));
        posPin = addPin(pinsTmp.remove(0));
        titleId = R.string.action_widget_state_title;
    }

    @Override
    protected void calculatePinValue(WorldState worldState, Task task) {
        PinBoolean value = (PinBoolean) getPinValue(worldState, task, statePin);
        MainAccessibilityService service = MainApplication.getService();
        AccessibilityNodeInfo root = service.getRootInActiveWindow();

        String id = ((PinString) getPinValue(worldState, task, idPin)).getValue();
        String level = ((PinString) getPinValue(worldState, task, levelPin)).getValue();
        PinPoint pinPoint = (PinPoint) getPinValue(worldState, task, posPin);

        if (!(id == null || id.isEmpty())) {
            List<AccessibilityNodeInfo> nodeInfos = root.findAccessibilityNodeInfosByViewId(root.getPackageName() + ":" + id);
            if (nodeInfos.size() > 0) {
                setPosPin(pinPoint, nodeInfos.get(0));
                value.setValue(true);
                return;
            }
        }

        if (!(level == null || level.isEmpty())) {
            String[] levels = level.split(",");
            for (String lv : levels) {
                int l = 0;
                try {
                    l = Integer.parseInt(lv);
                } catch (NumberFormatException ignored) {
                }
                root = searchNode(root, l);
                if (root == null) break;
            }

            if (root != null) {
                setPosPin(pinPoint, root);
                value.setValue(true);
                return;
            }
        }

        value.setValue(false);
    }

    private void setPosPin(PinPoint point, AccessibilityNodeInfo nodeInfo) {
        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);
        point.setX(bounds.centerX());
        point.setY(bounds.centerY());
    }

    private AccessibilityNodeInfo searchNode(AccessibilityNodeInfo nodeInfo, int level) {
        int index = 0;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                if (level == index) {
                    return child;
                } else {
                    index++;
                }
            }
        }
        return null;
    }
}
