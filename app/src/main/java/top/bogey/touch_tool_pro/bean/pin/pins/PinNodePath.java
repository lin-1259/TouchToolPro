package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.ui.picker.NodePickerItemInfo;

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
                if (child == null) child = root;
                else {
                    NodePath nodePath = new NodePath(path.trim(), params);
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

    public void setValue(NodePickerItemInfo info) {
        if (info == null) {
            value = null;
            return;
        }

        LinkedList<NodePath> paths = new LinkedList<>();
        while (info != null) {
            paths.addFirst(new NodePath(info));
            info = info.parent;
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
        private int index = 1;
        private String id;

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
            }
        }

        public NodePath(NodePickerItemInfo info) {
            cls = info.cls;
            id = info.id;

            if (info.parent != null) {
                ArrayList<NodePickerItemInfo> children = info.parent.children;
                for (int i = 0; i < children.size(); i++) {
                    NodePickerItemInfo child = children.get(i);
                    if (child != null && child.equals(info)) {
                        index = i + 1;
                        break;
                    }
                }
            }
        }

        public NodePath(String path, HashMap<String, Integer> params) {
            Pattern pattern = Pattern.compile("^([a-zA-Z0-9.]+)$");
            // 代表没有任何额外信息的节点
            if (pattern.matcher(path).find()) {
                cls = path;
            } else {
                pattern = Pattern.compile("^(.+?)(\\[.+])$");
                Matcher matcher = pattern.matcher(path);
                if (matcher.find()) {
                    cls = matcher.group(1);
                    String detail = matcher.group(2);
                    if (detail == null) return;

                    String[] strings = detail.split("\\[");
                    for (String string : strings) {
                        if (string.isEmpty()) continue;
                        List<String> regexes = Arrays.asList("id=(.+)]", "(\\d+)]", "\\{(\\S*)\\}]");
                        for (int i = 0; i < regexes.size(); i++) {
                            String regex = regexes.get(i);
                            pattern = Pattern.compile(regex);
                            matcher = pattern.matcher(string);
                            if (matcher.find()) {
                                switch (i) {
                                    case 0 -> id = matcher.group(1);
                                    case 1 -> index = Integer.parseInt(Objects.requireNonNull(matcher.group(1)));
                                    case 2 -> {
                                        Integer integer = params.get(matcher.group(1));
                                        index = integer == null ? 1 : integer;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        private boolean checkId(AccessibilityNodeInfo node) {
            String resourceName = node.getViewIdResourceName();
            if ((id == null || id.isEmpty()) && (resourceName == null || resourceName.isEmpty())) {
                return true;
            }
            return Objects.equals(id, resourceName);
        }

        private boolean checkClass(AccessibilityNodeInfo node) {
            if (cls == null) return false;
            return cls.contentEquals(node.getClassName());
        }

        public AccessibilityNodeInfo getChildNode(AccessibilityNodeInfo root) {
            AccessibilityNodeInfo child = null;
            // 类型一定得匹配上
            // 先根据层级，id，类型获取
            if (index > 0 && index <= root.getChildCount()) {
                AccessibilityNodeInfo node = root.getChild(index - 1);
                if (node != null) {
                    if (checkId(node) && checkClass(node)) child = node;
                }
            }

            // 获取失败了，就根据id和类型去获取
            if (child == null) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    AccessibilityNodeInfo node = root.getChild(i);
                    if (node == null) continue;
                    if (checkId(node) && checkClass(node)) {
                        child = node;
                        break;
                    }
                }
            }

            // 再不行就根据层级和类型去获取
            if (child == null) {
                if (index > 0 && index <= root.getChildCount()) {
                    AccessibilityNodeInfo node = root.getChild(index - 1);
                    if (node != null) {
                        if (checkClass(node)) child = node;
                    }
                }
            }

            // 实在不行就随便获取一个吧
            if (child == null) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    AccessibilityNodeInfo node = root.getChild(i);
                    if (node == null) continue;
                    if (checkClass(node)) {
                        child = node;
                        break;
                    }
                }
            }

            return child;
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
