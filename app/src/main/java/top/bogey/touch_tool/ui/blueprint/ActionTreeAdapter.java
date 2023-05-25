package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
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

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.ActionMap;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.ViewCardListItemBinding;
import top.bogey.touch_tool.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool.utils.DisplayUtils;

public class ActionTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final CardLayoutView cardLayoutView;

    public ActionTreeAdapter(CardLayoutView cardLayoutView, TreeNodeManager manager) {
        super(null, manager);
        this.manager = manager;
        this.cardLayoutView = cardLayoutView;

        setTreeNodeClickListener((treeNode, view) -> {
            if (treeNode.getLevel() == 1) {
                ActionMap.ActionInfo actionInfo = (ActionMap.ActionInfo) treeNode.getValue();
                cardLayoutView.addAction(actionInfo.getCls());
            }
        });

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
        LinkedHashMap<ActionMap.ActionType, ArrayList<ActionMap.ActionInfo>> actions = ActionMap.getInstance().getActions();
        ActionContext actionContext = cardLayoutView.getActionContext();
        actions.forEach((type, actionInfo) -> {
            if (actionContext instanceof BaseFunction && type == ActionMap.ActionType.START) {
                return;
            }
            TreeNode treeNode = new TreeNode(type, R.layout.view_card_list_type_item);
            for (ActionMap.ActionInfo info : actionInfo) {
                TreeNode node = new TreeNode(info, R.layout.view_card_list_item);
                treeNode.addChild(node);
            }
            treeNodes.add(treeNode);
        });

        updateTreeNodes(treeNodes);
    }

    protected static class ViewHolder extends TreeViewHolder {
        private ViewCardListTypeItemBinding typeBinding;
        private ViewCardListItemBinding itemBinding;
        private final Context context;

        public ViewHolder(@NonNull ViewCardListTypeItemBinding binding) {
            super(binding.getRoot());
            typeBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);
        }

        public ViewHolder(@NonNull ViewCardListItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(Math.round(DisplayUtils.dp2px(context, 8)));
            itemBinding.icon.setVisibility(View.VISIBLE);
        }

        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            if (level == 0) {
                ActionMap.ActionType type = (ActionMap.ActionType) node.getValue();
                typeBinding.title.setText(type.getTitle(context));
            } else if (level == 1) {
                ActionMap.ActionInfo actionInfo = (ActionMap.ActionInfo) node.getValue();
                itemBinding.title.setText(context.getString(actionInfo.getTitle()));
                itemBinding.icon.setImageResource(actionInfo.getIcon());
            }
        }
    }
}
