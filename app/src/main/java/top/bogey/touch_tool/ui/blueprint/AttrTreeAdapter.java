package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.attribute.GetValueAction;
import top.bogey.touch_tool.data.action.attribute.SetValueAction;
import top.bogey.touch_tool.data.pin.PinMap;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.ViewCardListAttrItemBinding;
import top.bogey.touch_tool.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;

public class AttrTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final CardLayoutView cardLayoutView;

    private final TreeNode attrTreeNode;
    private final ArrayMap<Class<? extends PinObject>, Integer> map = PinMap.getInstance().getMap();

    public AttrTreeAdapter(CardLayoutView cardLayoutView, TreeNodeManager manager) {
        super(null, manager);
        this.manager = manager;
        this.cardLayoutView = cardLayoutView;
        attrTreeNode = new TreeNode(cardLayoutView.getContext().getString(R.string.attribute_title), R.layout.view_card_list_type_item);
        initRoot();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        cardLayoutView.getActionContext().save();
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
        return new ViewHolder(ViewCardListAttrItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void initRoot() {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();

        HashMap<String, PinObject> attrs = cardLayoutView.getActionContext().getAttrs();
        attrs.forEach((key, value) -> {
            TreeNode node = new TreeNode(new TreeNodeInfo(key, value), R.layout.view_card_list_attr_item);
            attrTreeNode.addChild(node);
        });
        treeNodes.add(attrTreeNode);

        updateTreeNodes(treeNodes);
        expandAll();
    }

    protected class ViewHolder extends TreeViewHolder {
        private ViewCardListTypeItemBinding typeBinding;
        private ViewCardListAttrItemBinding itemBinding;
        private final Context context;

        public ViewHolder(@NonNull ViewCardListTypeItemBinding binding) {
            super(binding.getRoot());
            typeBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);

            typeBinding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(context, R.string.attribute_add, null, result -> {
                if (result != null && result.length() > 0) {
                    ActionContext actionContext = cardLayoutView.getActionContext();
                    if (actionContext.getAttr(result.toString()) != null) {
                        Toast.makeText(context, R.string.attribute_add_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PinObject value = new PinString();
                    actionContext.addAttr(result.toString(), value);
                    actionContext.save();
                    TreeNodeInfo treeNodeInfo = new TreeNodeInfo(result.toString(), value);
                    TreeNode node = new TreeNode(treeNodeInfo, R.layout.view_card_list_item);
                    attrTreeNode.addChild(node);
                    notifyDataSetChanged();
                }
            }));
        }

        public ViewHolder(@NonNull ViewCardListAttrItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(DisplayUtils.dp2px(context, 8));

            binding.removeButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                ArrayList<BaseAction> actions = getValueActions(treeNodeInfo.getKey());
                if (actions.size() > 0) {
                    AppUtils.showDialog(context, R.string.delete_attribute_tips, result -> {
                        if (result) {
                            for (BaseAction action : actions) {
                                cardLayoutView.removeAction(action);
                            }
                            removeAttribute();
                        }
                    });
                } else {
                    removeAttribute();
                }
            });

            binding.getAttrButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                cardLayoutView.addAction(GetValueAction.class, treeNodeInfo.getKey(), treeNodeInfo.getValue());
            });

            binding.setAttrButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                cardLayoutView.addAction(SetValueAction.class, treeNodeInfo.getKey(), treeNodeInfo.getValue());
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
            for (Integer id : map.values()) {
                adapter.add(context.getString(id));
            }
            binding.spinner.setAdapter(adapter);
            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Class<? extends PinObject> aClass = map.keyAt(position);
                    int index = getBindingAdapterPosition();
                    TreeNode treeNode = manager.get(index);
                    TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                    if (aClass.equals(treeNodeInfo.getValue().getClass())) return;

                    try {
                        ActionContext actionContext = cardLayoutView.getActionContext();
                        PinObject pinObject = aClass.newInstance();
                        treeNodeInfo.setValue(pinObject);
                        actionContext.addAttr(treeNodeInfo.getKey(), pinObject);
                        for (BaseAction action : getValueActions(treeNodeInfo.getKey())) {
                            if (action instanceof SetValueAction) ((SetValueAction) action).setValue(pinObject.copy());
                            if (action instanceof GetValueAction) ((GetValueAction) action).setValue(pinObject.copy());
                            cardLayoutView.refreshValueActionPins(action);
                        }
                        actionContext.save();
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private ArrayList<BaseAction> getValueActions(String key) {
            ActionContext actionContext = cardLayoutView.getActionContext();
            ArrayList<BaseAction> actions = new ArrayList<>();
            for (BaseAction action : actionContext.getActionsByClass(SetValueAction.class)) {
                if (((SetValueAction) action).getKey().equals(key)) {
                    actions.add(action);
                }
            }
            for (BaseAction action : actionContext.getActionsByClass(GetValueAction.class)) {
                if (((GetValueAction) action).getKey().equals(key)) {
                    actions.add(action);
                }
            }
            return actions;
        }

        private void removeAttribute() {
            ActionContext actionContext = cardLayoutView.getActionContext();
            int index = getBindingAdapterPosition();
            TreeNode treeNode = manager.get(index);
            manager.removeNode(treeNode);
            attrTreeNode.getChildren().remove(treeNode);
            notifyItemRemoved(index);

            TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
            actionContext.removeAttr(treeNodeInfo.getKey());
            actionContext.save();
        }

        @SuppressLint("DefaultLocale")
        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            if (level == 0) {
                String type = (String) node.getValue();
                typeBinding.title.setText(type);
                typeBinding.addButton.setVisibility(View.VISIBLE);
            } else if (level == 1) {
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) node.getValue();
                if (treeNodeInfo.getTitle() == null) {
                    itemBinding.title.setText(treeNodeInfo.getKey());
                    itemBinding.spinner.setSelection(map.indexOfKey(treeNodeInfo.getValue().getClass()));
                }
            }
        }
    }

    private static class TreeNodeInfo {
        private final String key;
        private PinObject value;
        private String title;

        public TreeNodeInfo(String key, PinObject value) {
            this.key = key;
            this.value = value;
        }

        public TreeNodeInfo(String key, String title) {
            this.key = key;
            this.title = title;
        }

        public String getKey() {
            return key;
        }

        public PinObject getValue() {
            return value;
        }

        public void setValue(PinObject value) {
            this.value = value;
        }

        public String getTitle() {
            return title;
        }
    }
}
