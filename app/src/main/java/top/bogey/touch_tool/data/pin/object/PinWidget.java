package top.bogey.touch_tool.data.pin.object;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Objects;

import top.bogey.touch_tool.utils.GsonUtils;

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
        id = GsonUtils.getAsString(jsonObject, "id", null);
        level = GsonUtils.getAsString(jsonObject, "level", null);
    }

    public AccessibilityNodeInfo getNode(Rect screenSize, AccessibilityNodeInfo root, boolean justScreen) {
        if (root == null) return null;

        if (!(id == null || id.isEmpty())) {
            List<AccessibilityNodeInfo> nodeInfo = root.findAccessibilityNodeInfosByViewId(root.getPackageName() + ":" + id);
            // 仅有一个才是正确的，有多个的话，需要看层级
            Rect rect = new Rect();
            AccessibilityNodeInfo accessibilityNodeInfo = null;
            for (AccessibilityNodeInfo node : nodeInfo) {
                node.getBoundsInScreen(rect);
                if (justScreen && !Rect.intersects(screenSize, rect)) continue;
                if (accessibilityNodeInfo == null) accessibilityNodeInfo = node;
                else {
                    accessibilityNodeInfo = null;
                    break;
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
                if (justScreen && !Rect.intersects(screenSize, rect)) node = null;
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

    @Override
    public boolean isEmpty() {
        return id == null && level == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinWidget pinWidget = (PinWidget) o;

        if (!Objects.equals(id, pinWidget.id)) return false;
        return Objects.equals(level, pinWidget.level);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (level != null ? level.hashCode() : 0);
        return result;
    }
}
