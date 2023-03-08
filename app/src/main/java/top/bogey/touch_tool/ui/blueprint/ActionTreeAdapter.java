package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionMap;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.ViewCardListItemBinding;
import top.bogey.touch_tool.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;

public class ActionTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final TreeNode functionTreeNode;
    private BaseFunction excludeFunction;

    public ActionTreeAdapter(CardLayoutView cardLayoutView, TreeNodeManager manager) {
        super(null, manager);
        this.manager = manager;

        setTreeNodeClickListener((treeNode, view) -> {
            if (treeNode.getLevel() == 1) {
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                if (treeNodeInfo.getId() == null) {
                    cardLayoutView.addAction(treeNodeInfo.getaClass());
                } else {
                    cardLayoutView.addAction(treeNodeInfo.getId());
                }
            }
        });

        if (cardLayoutView.getActionContext() instanceof BaseFunction) {
            excludeFunction = (BaseFunction) cardLayoutView.getActionContext();
        }

        functionTreeNode = new TreeNode(ActionMap.ActionType.CUSTOM, R.layout.view_card_list_type_item);
        initRoot();
    }

    @Override
    public void onBindViewHolder(@NonNull TreeViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ((ViewHolder) holder).refreshItem(manager.get(position));
    }

    @SuppressLint("NonConstantResourceId")
    @NonNull
    @Override
    public TreeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int layoutId) {
        if (layoutId == R.layout.view_card_list_type_item) {
            return new ViewHolder(ViewCardListTypeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        return new ViewHolder(ViewCardListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void initRoot() {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        LinkedHashMap<ActionMap.ActionType, LinkedHashMap<Class<? extends BaseAction>, Integer>> actions = ActionMap.getInstance().getActions();
        actions.forEach((type, actionInfo) -> {
            TreeNode treeNode = new TreeNode(type, R.layout.view_card_list_type_item);
            for (Map.Entry<Class<? extends BaseAction>, Integer> entry : actionInfo.entrySet()) {
                TreeNodeInfo treeNodeInfo = new TreeNodeInfo(entry.getKey(), entry.getValue());
                TreeNode node = new TreeNode(treeNodeInfo, R.layout.view_card_list_item);
                treeNode.addChild(node);
            }
            treeNodes.add(treeNode);
        });

        LinkedHashMap<String, BaseFunction> functions = TaskRepository.getInstance().getFunctions();
        functions.forEach((id, function) -> {
            if (excludeFunction != null && id.equals(excludeFunction.getId())) return;
            TreeNodeInfo treeNodeInfo = new TreeNodeInfo(id, function.getTitle());
            TreeNode node = new TreeNode(treeNodeInfo, R.layout.view_card_list_item);
            functionTreeNode.addChild(node);
        });
        treeNodes.add(functionTreeNode);

        updateTreeNodes(treeNodes);
    }

    protected class ViewHolder extends TreeViewHolder {
        private ViewCardListTypeItemBinding typeBinding;
        private ViewCardListItemBinding itemBinding;
        private final Context context;

        public ViewHolder(@NonNull ViewCardListTypeItemBinding binding) {
            super(binding.getRoot());
            typeBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);

            typeBinding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(context, R.string.function_add, null, result -> {
                if (result != null && result.length() > 0) {
                    BaseFunction function = new BaseFunction(context);
                    function.setTitle(result.toString());
                    function.save();
                    TreeNodeInfo treeNodeInfo = new TreeNodeInfo(function.getFunctionId(), function.getTitle());
                    TreeNode node = new TreeNode(treeNodeInfo, R.layout.view_card_list_item);
                    functionTreeNode.addChild(node);
                    notifyDataSetChanged();
                }
            }));
        }

        public ViewHolder(@NonNull ViewCardListItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(DisplayUtils.dp2px(context, 8));

            binding.removeButton.setOnClickListener(v -> AppUtils.showDialog(context, R.string.delete_function_tips, result -> {
                if (result) {
                    int index = getBindingAdapterPosition();
                    TreeNode treeNode = manager.get(index);
                    manager.removeNode(treeNode);
                    notifyItemRemoved(index);
                    functionTreeNode.getChildren().remove(treeNode);
                    TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                    TaskRepository.getInstance().removeFunction(treeNodeInfo.getId());
                }
            }));

            binding.editButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();

                Intent intent = new Intent(context.getApplicationContext(), FunctionBlueprintActivity.class);
                intent.putExtra("functionId", treeNodeInfo.getId());
                context.startActivity(intent);
            });
        }

        @SuppressLint("DefaultLocale")
        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            if (level == 0) {
                ActionMap.ActionType type = (ActionMap.ActionType) node.getValue();
                typeBinding.title.setText(type.getTitle(context));
                typeBinding.addButton.setVisibility(type == ActionMap.ActionType.CUSTOM ? View.VISIBLE : View.GONE);
            } else if (level == 1) {
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) node.getValue();
                if (treeNodeInfo.getId() == null) {
                    itemBinding.title.setText(context.getString(treeNodeInfo.getTitleId()));
                } else {
                    itemBinding.title.setText(treeNodeInfo.getTitle());
                }
                ActionMap.ActionType type = (ActionMap.ActionType) node.getParent().getValue();
                itemBinding.editButton.setVisibility(type == ActionMap.ActionType.CUSTOM ? View.VISIBLE : View.GONE);
                itemBinding.removeButton.setVisibility(type == ActionMap.ActionType.CUSTOM ? View.VISIBLE : View.GONE);
            }
        }
    }

    private static class TreeNodeInfo {
        private Class<? extends BaseAction> aClass;
        private String id;
        private String title;
        private int titleId;

        public TreeNodeInfo(Class<? extends BaseAction> aClass, int titleId) {
            this.aClass = aClass;
            this.titleId = titleId;
        }

        public TreeNodeInfo(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public Class<? extends BaseAction> getaClass() {
            return aClass;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getTitleId() {
            return titleId;
        }
    }
}
