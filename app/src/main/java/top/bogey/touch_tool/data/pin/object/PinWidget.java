package top.bogey.touch_tool.data.pin.object;

import android.view.accessibility.AccessibilityNodeInfo;

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
        id = jsonObject.get("id").getAsString();
        level = jsonObject.get("level").getAsString();
    }

    public AccessibilityNodeInfo getNode(AccessibilityNodeInfo root) {
        if (!(id == null || id.isEmpty())) {
            List<AccessibilityNodeInfo> nodeInfo = root.findAccessibilityNodeInfosByViewId(root.getPackageName() + ":" + id);
            if (nodeInfo.size() > 0) {
                return nodeInfo.get(0);
            }
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
