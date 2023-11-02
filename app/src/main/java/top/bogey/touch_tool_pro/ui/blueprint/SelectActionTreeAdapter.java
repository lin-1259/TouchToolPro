package top.bogey.touch_tool_pro.ui.blueprint;

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

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionMap;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.action.var.GetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.GetVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetVariableValue;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.databinding.ViewCardListItemBinding;
import top.bogey.touch_tool_pro.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;

public class SelectActionTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final LinkedHashMap<ActionMap, ArrayList<Object>> types;

    public SelectActionTreeAdapter(CardLayoutView cardLayoutView, TreeNodeManager manager, LinkedHashMap<ActionMap, ArrayList<Object>> types) {
        super(null, manager);
        this.manager = manager;
        this.types = types;

        setTreeNodeClickListener((node, view) -> {
            if (node.getLevel() == 1) {
                if (node.getValue() instanceof ActionType type) {
                    cardLayoutView.addAction(type.getActionClass());
                } else if (node.getValue() instanceof String functionId) {
                    cardLayoutView.addAction(functionId);
                } else if (node.getValue() instanceof Function function) {
                    cardLayoutView.addAction(function);
                } else if (node.getValue() instanceof SelectActionDialog.VariableInfo variableInfo) {
                    if (variableInfo.from == 1) {
                        cardLayoutView.addAction(variableInfo.out ? GetCommonVariableValue.class : SetCommonVariableValue.class, variableInfo.key, variableInfo.value);
                    } else {
                        cardLayoutView.addAction(variableInfo.out ? GetVariableValue.class : SetVariableValue.class, variableInfo.key, variableInfo.value);
                    }
                } else if (node.getValue() instanceof ActionCard<?> card) {
                    cardLayoutView.tryLinkDragPin(card.getAction());
                }
                cardLayoutView.dismissDialog();
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
        } else {
            return new ViewHolder(ViewCardListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    public void initRoot() {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();

        types.forEach(((actionMap, actionTypes) -> {
            TreeNode treeNode = new TreeNode(actionMap, R.layout.view_card_list_type_item);
            actionTypes.forEach(type -> {
                TreeNode node = new TreeNode(type, R.layout.view_card_list_item);
                treeNode.addChild(node);
            });
            treeNodes.add(treeNode);
        }));

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
            setNodePadding(0);
            itemBinding.icon.setVisibility(View.VISIBLE);
        }

        @SuppressLint("SetTextI18n")
        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            if (level == 0) {
                ActionMap actionMap = (ActionMap) node.getValue();
                typeBinding.title.setText(actionMap.getTitle());
            } else if (level == 1) {
                if (node.getValue() instanceof ActionType type) {
                    itemBinding.title.setText(type.getTitle());
                    itemBinding.icon.setImageResource(type.getIcon());
                } else if (node.getValue() instanceof String functionId) {
                    Function function = SaveRepository.getInstance().getFunctionById(functionId);
                    if (function != null) {
                        itemBinding.title.setText(function.getTitle());
                        itemBinding.icon.setImageResource(R.drawable.icon_stop);
                    }
                } else if (node.getValue() instanceof Function function) {
                    itemBinding.title.setText(function.getTitle());
                    itemBinding.icon.setImageResource(0);
                } else if (node.getValue() instanceof SelectActionDialog.VariableInfo variableInfo) {
                    itemBinding.title.setText(variableInfo.key);
                    if (variableInfo.from == 1) itemBinding.icon.setImageResource(R.drawable.icon_stop);
                    else if (variableInfo.from == 2) itemBinding.icon.setImageResource(R.drawable.icon_play);
                    else itemBinding.icon.setImageResource(0);
                } else if (node.getValue() instanceof ActionCard<?> card) {
                    Action action = card.getAction();
                    itemBinding.title.setText(action.getTitle() + "(" + action.getX() + "," + action.getY() + ")");
                    itemBinding.icon.setImageResource(action.getType().getIcon());
                }
            }
        }
    }
}
