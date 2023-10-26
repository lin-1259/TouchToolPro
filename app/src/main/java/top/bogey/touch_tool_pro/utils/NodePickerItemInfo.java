package top.bogey.touch_tool_pro.utils;

import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;

public class NodePickerItemInfo {
    public String cls;
    public String id;
    public String text;
    public boolean clickable;
    public boolean checkable;
    public boolean editable;
    public boolean longClickable;
    public boolean visible;
    public Rect rect = new Rect();
    public NodePickerItemInfo parent;
    public ArrayList<NodePickerItemInfo> children = new ArrayList<>();

    public NodePickerItemInfo(AccessibilityNodeInfo nodeInfo) {

        cls = nodeInfo.getClassName().toString();
        id = nodeInfo.getViewIdResourceName();
        CharSequence nodeInfoText = nodeInfo.getText();
        if (nodeInfoText != null) text = nodeInfoText.toString();
        clickable = nodeInfo.isClickable();
        checkable = nodeInfo.isCheckable();
        editable = nodeInfo.isEditable();
        longClickable = nodeInfo.isLongClickable();
        visible = nodeInfo.isVisibleToUser();
        nodeInfo.getBoundsInScreen(rect);
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child == null) {
                children.add(null);
            } else {
                NodePickerItemInfo info = new NodePickerItemInfo(child);
                info.parent = this;
                children.add(info);
            }
        }
    }

    public ArrayList<NodePickerItemInfo> findChildrenById(String id) {
        ArrayList<NodePickerItemInfo> items = new ArrayList<>();
        for (NodePickerItemInfo child : children) {
            if (child == null) continue;
            if (id.equals(child.id)) items.add(child);
            items.addAll(child.findChildrenById(id));
        }
        return items;
    }

    public boolean isUsable() {
        return (clickable || checkable || editable || longClickable) && visible;
    }
}
