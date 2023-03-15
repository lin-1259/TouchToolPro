package top.bogey.touch_tool.data.pin.object;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class PinWidget extends PinValue {
    private String id;
    private String level;

    public PinWidget() {
        super();
    }

    public PinWidget(String id, String level) {
        super();
        this.id = id;
        this.level = level;
    }

    public PinWidget(JsonObject jsonObject) {
        super(jsonObject);
        JsonElement idElement = jsonObject.get("id");
        if (idElement != null) id = idElement.getAsString();
        JsonElement levelElement = jsonObject.get("level");
        if (levelElement != null) level = levelElement.getAsString();
    }

    public AccessibilityNodeInfo getNode(Rect screenSize, AccessibilityNodeInfo root) {
        if (root == null) return null;

        if (!(id == null || id.isEmpty())) {
            List<AccessibilityNodeInfo> nodeInfo = root.findAccessibilityNodeInfosByViewId(root.getPackageName() + ":" + id);
            // 仅有一个才是正确的，有多个的话，需要看层级
            Rect rect = new Rect();
            AccessibilityNodeInfo accessibilityNodeInfo = null;
            for (AccessibilityNodeInfo node : nodeInfo) {
                node.getBoundsInScreen(rect);
                if (Rect.intersects(screenSize, rect)) {
                    if (accessibilityNodeInfo == null) accessibilityNodeInfo = node;
                    else {
                        accessibilityNodeInfo = null;
                        break;
                    }
                }
            }
            if (accessibilityNodeInfo != null) return accessibilityNodeInfo;
        }

        if (!(level == null || level.isEmpty())) {
            String[] levels = level.split(",");
            AccessibilityNodeInfo node = root;
            for (String lv : levels) {
                int l = 0;
                try {
                    l = Integer.parseInt(lv);
                } catch (NumberFormatException ignored) {
                }
                node = searchNode(node, l);
                if (node == null) break;
            }
            if (node != null) {
                Rect rect = new Rect();
                node.getBoundsInScreen(rect);
                if (!Rect.intersects(screenSize, rect)) node = null;
            }
            return node;
        }

        return null;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
