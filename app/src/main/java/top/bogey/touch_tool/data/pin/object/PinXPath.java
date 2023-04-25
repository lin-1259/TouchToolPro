package top.bogey.touch_tool.data.pin.object;

import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.utils.GsonUtils;

public class PinXPath extends PinString {
    private String path;

    public PinXPath() {
        super();
    }

    public PinXPath(String path) {
        super();
        this.path = path;
    }

    public PinXPath(JsonObject jsonObject) {
        super(jsonObject);
        path = GsonUtils.getAsString(jsonObject, "path", null);
    }

    public void setPath(AccessibilityNodeInfo node) {
        if (node == null) {
            path = null;
            return;
        }

        LinkedList<XPath> paths = new LinkedList<>();

        while (node != null) {
            paths.addFirst(new XPath(node));
            node = node.getParent();
        }

        StringBuilder builder = new StringBuilder();
        for (XPath path : paths) {
            builder.append(path.toString());
            builder.append("\n");
        }
        path = builder.toString();
    }

    public AccessibilityNodeInfo getPathNode(ArrayList<AccessibilityNodeInfo> roots, HashMap<String, Integer> params) {
        if (path == null) return null;

        String[] paths = path.split("\n");

        for (AccessibilityNodeInfo root : roots) {
            AccessibilityNodeInfo child = null;
            for (String path : paths) {
                if (path.isEmpty()) continue;
                XPath xp = new XPath(path.trim(), params);
                if (child == null) {
                    child = root;
                } else {
                    child = xp.getChildNode(child);
                    if (child == null) break;
                }
            }
            if (root.equals(child)) return null;
            if (child != null) return child;
        }

        return null;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean isEmpty() {
        return path == null || path.isEmpty();
    }

    @NonNull
    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PinXPath pinXPath = (PinXPath) o;

        return Objects.equals(path, pinXPath.path);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    public static class XPath {
        private String cls;
        private String id;
        private int index;

        public XPath(AccessibilityNodeInfo node) {
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

        public XPath(String path, HashMap<String, Integer> params) {
            Pattern pattern = Pattern.compile("^([a-zA-Z.]+)(\\[*.*?]*)$");
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

        public String getCls() {
            return cls;
        }

        public int getIndex() {
            return index;
        }

        public String getId() {
            return id;
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
