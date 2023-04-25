package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinWidget extends PinValue {
    private String id;
    private String level;

    public PinWidget() {
        super();
    }

    public PinWidget(JsonObject jsonObject) {
        super(jsonObject);
        id = GsonUtils.getAsString(jsonObject, "id", null);
        level = GsonUtils.getAsString(jsonObject, "level", null);
    }

    public AccessibilityNodeInfo getNode(Rect screenSize, ArrayList<AccessibilityNodeInfo> roots, boolean justScreen) {
        if (roots == null || roots.size() == 0) return null;

        for (int i = roots.size() - 1; i >= 0; i--) {
            AccessibilityNodeInfo root = roots.get(i);
            if (root == null) continue;

            if (!(id == null || id.isEmpty())) {
                String searchId;
                if (id.startsWith("id/")) {
                    searchId = root.getPackageName() + ":" + id;
                } else {
                    searchId = id;
                }
                ArrayList<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
                searchNode(nodeInfoList, root, searchId);

                // 仅有一个才是正确的，有多个的话，需要看层级
                Rect rect = new Rect();
                AccessibilityNodeInfo accessibilityNodeInfo = null;
                for (AccessibilityNodeInfo node : nodeInfoList) {
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

                // id校验
                if (node != null && id != null && !id.isEmpty()) {
                    String name = node.getViewIdResourceName();
                    if (name != null && !name.isEmpty()) {
                        if (!name.contains(id)) node = null;
                    }
                }

                // 区域校验
                if (node != null) {
                    Rect rect = new Rect();
                    node.getBoundsInScreen(rect);
                    if (justScreen && !Rect.intersects(screenSize, rect)) node = null;
                }

                if (node != null) return node;
            }
        }

        return null;
    }

    private void searchNode(ArrayList<AccessibilityNodeInfo> list, AccessibilityNodeInfo nodeInfo, String id) {
        if (nodeInfo == null) return;
        String idResourceName = nodeInfo.getViewIdResourceName();
        if (id.equals(idResourceName)) {
            list.add(nodeInfo);
        }
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            searchNode(list, child, id);
        }
    }

    private AccessibilityNodeInfo searchNode(AccessibilityNodeInfo nodeInfo, int level) {
        if (nodeInfo == null) return null;
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

    @NonNull
    @Override
    public String toString() {
        return "id=" + id + ",level=" + level;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.WidgetPinColor);
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
