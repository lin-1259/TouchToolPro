package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;

public class PinNodePath extends PinString {

    public PinNodePath() {
        super(PinType.NODE_PATH);
    }

    public PinNodePath(JsonObject jsonObject) {
        super(jsonObject);
    }

    public PinNodePath(String path) {
        super(PinType.NODE_PATH, path);
    }

    public AccessibilityNodeInfo getNode(ArrayList<AccessibilityNodeInfo> roots) {
        return getNode(roots, null);
    }

    public AccessibilityNodeInfo getNode(ArrayList<AccessibilityNodeInfo> roots, HashMap<String, Integer> params) {
        if (value == null) return null;

        String[] paths = value.split("\n");
        for (AccessibilityNodeInfo root : roots) {
            AccessibilityNodeInfo child = null;
            for (String path : paths) {
                if (path.isEmpty()) continue;
                NodePath nodePath = new NodePath(path.trim(), params);
                if (child == null) child = root;
                else {
                    child = nodePath.getChildNode(child);
                    if (child == null) break;
                }
            }
            if (root.equals(child)) return null;
            if (child != null) return child;
        }

        return null;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.NodePathPinColor);
    }

    public void setValue(AccessibilityNodeInfo node) {
        if (node == null) {
            value = null;
            return;
        }

        LinkedList<NodePath> paths = new LinkedList<>();

        while (node != null) {
            paths.addFirst(new NodePath(node));
            node = node.getParent();
            if (paths.size() > Byte.MAX_VALUE) {
                value = null;
                return;
            }
        }

        StringBuilder builder = new StringBuilder();
        paths.forEach(path -> {
            builder.append(path.toString());
            builder.append("\n");
        });
        value = builder.toString().trim();
    }


    public static class NodePath {
        private String cls;
        private String id;
        private int index;

        public NodePath(AccessibilityNodeInfo node) {
            cls = node.getClassName().toString();
            id = node.getViewIdResourceName();

            AccessibilityNodeInfo parent = node.getParent();
            if (parent != null) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    AccessibilityNodeInfo child = parent.getChild(i);
                    if (child != null && child.equals(node)) {
                        index = i + 1;
                        break;
                    }
                }

                List<AccessibilityNodeInfo> nodeInfoList = parent.findAccessibilityNodeInfosByViewId(id);
                // id在路径中不唯一，就无法根据id去获取节点
                if (nodeInfoList.size() != 1) id = null;
            } else {
                id = null;
            }
        }

        public NodePath(String path, HashMap<String, Integer> params) {
            Pattern pattern = Pattern.compile("^([a-zA-Z0-9.]+)(\\[*.*?]*)$");
            Matcher matcher = pattern.matcher(path);
            if (matcher.find()) {
                cls = matcher.group(1);
                try {
                    String detail = matcher.group(2);
                    if (detail != null) {
                        String[] strings = detail.split("\\[");
                        for (String string : strings) {
                            if (string.isEmpty()) continue;
                            string = string.replace("]", "");
                            try {
                                index = Integer.parseInt(string);
                            } catch (NumberFormatException ignored) {
                                if (string.contains("id=")) {
                                    id = string.replace("id=", "");
                                } else {
                                    if (params != null) {
                                        string = string.replace("{", "");
                                        string = string.replace("}", "");
                                        Integer integer = params.get(string);
                                        index = integer == null ? 0 : integer;
                                    }
                                }
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }

        public AccessibilityNodeInfo getChildNode(AccessibilityNodeInfo root) {
            AccessibilityNodeInfo child = null;
            if (id != null) {
                List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(id);
                if (nodes.size() > 0) {
                    child = nodes.get(0);
                }
            } else {
                try {
                    child = root.getChild(index - 1);
                } catch (Exception ignored) {
                }
            }
            // 从id或层级拿的子节点需要判断类型是不是一样的
            if (child != null && child.getClassName().equals(cls)) {
                return child;
            } else {
                // 否则就获取第一个匹配的上cls的节点
                for (int i = 0; i < root.getChildCount(); i++) {
                    AccessibilityNodeInfo nodeInfo = root.getChild(i);
                    if (nodeInfo != null && nodeInfo.getClassName().equals(cls)) {
                        return nodeInfo;
                    }
                }
            }
            return null;
        }

        @NonNull
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(cls);
            if (id != null) builder.append("[id=").append(id).append("]");
            if (index > 1) builder.append("[").append(index).append("]");
            return builder.toString();
        }
    }
}
