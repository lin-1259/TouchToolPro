package top.bogey.touch_tool.ui.picker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import java.util.ArrayList;
import java.util.Collections;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.databinding.FloatPickerWidgetItemBinding;
import top.bogey.touch_tool.utils.DisplayUtils;

public class WidgetPickerTreeAdapter extends TreeViewAdapter {
    private TreeNode selectedNode;
    private TreeNode rootNode;
    private final TreeNodeManager manager;

    public WidgetPickerTreeAdapter(TreeNodeManager manager, SelectNode picker) {
        super(null, manager);
        this.manager = manager;
        setTreeNodeLongClickListener((treeNode, view) -> {
            AccessibilityNodeInfo nodeInfo = (AccessibilityNodeInfo) treeNode.getValue();
            picker.selectNode(nodeInfo);
            setSelectedNode(nodeInfo);
            return true;
        });
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder) holder).refreshItem(manager.get(position));
    }

    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        return new ViewHolder(FloatPickerWidgetItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void setRoot(AccessibilityNodeInfo root) {
        rootNode = createTree(root, 0);
        ArrayList<TreeNode> treeNodes = new ArrayList<>(Collections.singleton(rootNode));
        updateTreeNodes(treeNodes);
    }

    private TreeNode createTree(AccessibilityNodeInfo root, int level) {
        TreeNode node = new TreeNode(root, R.layout.float_picker_widget_item);
        node.setLevel(level);
        for (int i = 0; i < root.getChildCount(); i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child != null) {
                node.addChild(createTree(child, level + 1));
            }
        }
        return node;
    }

    public void setSelectedNode(AccessibilityNodeInfo node) {
        collapseAll();
        if (node == null) {
            selectedNode = null;
        } else {
            if (rootNode != null) {
                selectedNode = findTreeNode(rootNode, node);
                if (selectedNode != null) {
                    TreeNode parent = selectedNode.getParent();
                    while (parent != null) {
                        parent.setExpanded(true);
                        parent = parent.getParent();
                    }
                    notifyDataSetChanged();
                }
            }
        }
    }

    private TreeNode findTreeNode(TreeNode treeNode, Object value) {
        if (value.equals(treeNode.getValue())) return treeNode;
        for (TreeNode child : treeNode.getChildren()) {
            TreeNode node = findTreeNode(child, value);
            if (node != null) return node;
        }
        return null;
    }

    protected class ViewHolder extends TreeViewHolder {
        private final FloatPickerWidgetItemBinding binding;
        private final Context context;

        public ViewHolder(@NonNull FloatPickerWidgetItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();
        }

        @Override
        public void bindTreeNode(TreeNode node) {
            int padding = (int) (node.getLevel() * DisplayUtils.dp2px(context, 8));
            binding.contentBox.setPaddingRelative(padding, 0, 0, 0);
        }

        public void refreshItem(TreeNode node) {
            AccessibilityNodeInfo value = (AccessibilityNodeInfo) node.getValue();
            binding.titleText.setText(getNodeTitle(value));

            int color;
            if (value.isClickable()) {
                color = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0);
            } else {
                color = DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorOnSurface, 0);
            }
            binding.titleText.setTextColor(color);
            binding.imageView.setImageTintList(ColorStateList.valueOf(color));

            binding.imageView.setVisibility(node.getChildren().size() > 0 ? View.VISIBLE : View.INVISIBLE);
            binding.imageView.setImageResource(node.isExpanded() ? R.drawable.icon_up : R.drawable.icon_down);

            if (node.equals(selectedNode)) {
                binding.titleText.setTextColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorError, 0));
            }
        }

        private String getNodeTitle(AccessibilityNodeInfo node) {
            StringBuilder builder = new StringBuilder();
            builder.append(node.getClassName());
            CharSequence text = node.getText();
            if (text != null && text.length() > 0) {
                builder.append(" | ");
                builder.append(text);
            }

            String resourceName = node.getViewIdResourceName();
            if (resourceName != null && !resourceName.isEmpty()) {
                String[] split = resourceName.split(":");
                if (split.length > 1) {
                    builder.append(" [ ");
                    builder.append(split[1]);
                    builder.append(" ]");
                } else {
                    builder.append(resourceName);
                }
            }

            return builder.toString();
        }
    }

    public interface SelectNode {
        void selectNode(AccessibilityNodeInfo nodeInfo);
    }
}