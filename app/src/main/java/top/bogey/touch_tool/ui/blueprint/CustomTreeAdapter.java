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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskContext;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.attribute.GetValueAction;
import top.bogey.touch_tool.data.action.attribute.SetValueAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.pin.PinMap;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.ViewCardListAttrItemBinding;
import top.bogey.touch_tool.databinding.ViewCardListItemBinding;
import top.bogey.touch_tool.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool.ui.recorder.RecorderFloatView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;

public class CustomTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final BlueprintView blueprintView;
    private final CardLayoutView cardLayoutView;

    private final TreeNode commonFunctionTreeNode;
    private final TreeNode functionTreeNode;
    private final TreeNode attrTreeNode;
    private final TreeNode functionAttrTreeNode;

    private final ArrayMap<Class<? extends PinObject>, Integer> map = PinMap.getInstance().getMap();

    public CustomTreeAdapter(BlueprintView blueprintView, CardLayoutView cardLayoutView, TreeNodeManager manager) {
        super(null, manager);
        this.manager = manager;
        this.blueprintView = blueprintView;
        this.cardLayoutView = cardLayoutView;

        setTreeNodeClickListener((treeNode, view) -> {
            if (treeNode.getLevel() == 1) {
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                if (info.getType() == TreeNodeType.COMMON_FUNCTION || info.getType() == TreeNodeType.FUNCTION) {
                    if (info.getType() == TreeNodeType.COMMON_FUNCTION) {
                        cardLayoutView.addAction(info.getKey());
                    } else {
                        ActionContext actionContext = getRealActionContext(info);
                        if (actionContext instanceof TaskContext) {
                            TaskContext taskContext = (TaskContext) actionContext;
                            cardLayoutView.addAction(taskContext.getFunctionById(info.getKey()));
                        }
                    }
                } else {
                    cardLayoutView.addAction(GetValueAction.class, info.getKey(), info.getValue());
                }
            }
        });

        commonFunctionTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.COMMON_FUNCTION, blueprintView.getString(R.string.function_title_common)), R.layout.view_card_list_type_item);
        functionTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.FUNCTION, blueprintView.getString(R.string.function_title)), R.layout.view_card_list_type_item);
        attrTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.ATTR, blueprintView.getString(R.string.attribute_title)), R.layout.view_card_list_type_item);
        functionAttrTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.FUNCTION_ATTR, blueprintView.getString(R.string.attribute_title_function)), R.layout.view_card_list_type_item);

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
        } else if (layoutId == R.layout.view_card_list_item) {
            return new ViewHolder(ViewCardListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ViewHolder(ViewCardListAttrItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    public void initRoot() {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();

        // 通用自定义卡
        treeNodes.add(commonFunctionTreeNode);
        ArrayList<BaseFunction> functions = TaskRepository.getInstance().getFunctions();
        Collator collator = Collator.getInstance(Locale.CHINA);
        functions.sort((o1, o2) -> collator.compare(o1.getTitle(null), o2.getTitle(null)));
        functions.forEach(function -> {
            TreeNodeInfo info = new TreeNodeInfo(TreeNodeType.COMMON_FUNCTION, function.getFunctionId(), function.getTitle(cardLayoutView.getContext()));
            TreeNode node = new TreeNode(info, R.layout.view_card_list_item);
            commonFunctionTreeNode.addChild(node);
        });

        ActionContext actionContext = cardLayoutView.getActionContext();
        ActionContext parentContext = actionContext.getParent();
        ActionContext context = actionContext;
        if (parentContext != null) context = parentContext;

        if (context instanceof TaskContext) {
            // 当前任务或父任务的自定义卡
            treeNodes.add(functionTreeNode);
            functions = ((TaskContext) context).getFunctions();
            functions.sort((o1, o2) -> collator.compare(o1.getTitle(null), o2.getTitle(null)));
            functions.forEach(function -> {
                TreeNodeInfo info = new TreeNodeInfo(TreeNodeType.FUNCTION, function.getFunctionId(), function.getTitle(cardLayoutView.getContext()));
                TreeNode node = new TreeNode(info, R.layout.view_card_list_item);
                functionTreeNode.addChild(node);
            });

            // 当前任务或父任务的变量属性
            treeNodes.add(attrTreeNode);
            HashMap<String, PinObject> attrs = context.getAttrs();
            attrs.forEach((key, value) -> {
                TreeNodeInfo info = new TreeNodeInfo(TreeNodeType.ATTR, key, value);
                TreeNode node = new TreeNode(info, R.layout.view_card_list_attr_item);
                attrTreeNode.addChild(node);
            });
        }

        if (actionContext instanceof BaseFunction) {
            // 当前自定义卡的变量属性
            treeNodes.add(functionAttrTreeNode);
            HashMap<String, PinObject> attrs = actionContext.getAttrs();
            attrs.forEach((key, value) -> {
                TreeNodeInfo info = new TreeNodeInfo(TreeNodeType.FUNCTION_ATTR, key, value);
                TreeNode node = new TreeNode(info, R.layout.view_card_list_attr_item);
                functionAttrTreeNode.addChild(node);
            });
        }

        updateTreeNodes(treeNodes);
        expandAll();
    }

    public ActionContext getRealActionContext(TreeNodeInfo info) {
        ActionContext actionContext = cardLayoutView.getActionContext();
        ActionContext parentContext = actionContext.getParent();

        if (info.getType() == TreeNodeType.TYPE) {
            if (info.getSubType() == TreeNodeType.FUNCTION || info.getSubType() == TreeNodeType.ATTR) {
                if (parentContext != null) actionContext = parentContext;
            }
        } else {
            if (info.getType() == TreeNodeType.FUNCTION || info.getType() == TreeNodeType.ATTR) {
                if (parentContext != null) actionContext = parentContext;
            }
        }

        return actionContext;
    }

    protected class ViewHolder extends TreeViewHolder {
        private ViewCardListTypeItemBinding typeBinding;
        private ViewCardListItemBinding itemBinding;
        private ViewCardListAttrItemBinding attrBinding;
        private final Context context;

        public ViewHolder(@NonNull ViewCardListTypeItemBinding binding) {
            super(binding.getRoot());
            typeBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);

            typeBinding.recordButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                TreeNodeType subType = treeNodeInfo.getSubType();

                if (subType == TreeNodeType.FUNCTION) {
                    ActionContext actionContext = getRealActionContext(treeNodeInfo);
                    if (actionContext instanceof TaskContext) {
                        TaskContext taskContext = (TaskContext) actionContext;
                        BaseFunction function = new BaseFunction();
                        new RecorderFloatView(context, () -> {
                            function.setTitle(context.getString(R.string.record_default_title));
                            taskContext.addFunction(function);
                            taskContext.save();
                        }, function).show();
                    }

                }
            });

            typeBinding.addButton.setVisibility(View.VISIBLE);
            typeBinding.addButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo treeNodeInfo = (TreeNodeInfo) treeNode.getValue();
                TreeNodeType subType = treeNodeInfo.getSubType();

                if (subType == TreeNodeType.COMMON_FUNCTION || subType == TreeNodeType.FUNCTION) {
                    AppUtils.showEditDialog(context, R.string.function_add, null, result -> {
                        if (result != null && result.length() > 0) {
                            BaseFunction function = new BaseFunction();
                            function.setTitle(result.toString());

                            TreeNode parentTreeNode;
                            if (subType == TreeNodeType.COMMON_FUNCTION) {
                                parentTreeNode = commonFunctionTreeNode;
                            } else {
                                ActionContext actionContext = getRealActionContext(treeNodeInfo);
                                if (actionContext instanceof TaskContext) {
                                    ((TaskContext) actionContext).addFunction(function);
                                }
                                parentTreeNode = functionTreeNode;
                            }
                            function.save();

                            TreeNodeInfo info = new TreeNodeInfo(subType, function.getFunctionId(), function.getTitle(context));
                            TreeNode node = new TreeNode(info, R.layout.view_card_list_item);
                            parentTreeNode.addChild(node);
                            if (parentTreeNode.isExpanded()) manager.collapseNode(parentTreeNode);
                            manager.expandNode(parentTreeNode);
                            notifyDataSetChanged();
                        }
                    });
                } else if (subType == TreeNodeType.ATTR || subType == TreeNodeType.FUNCTION_ATTR) {
                    AppUtils.showEditDialog(context, R.string.attribute_add, null, result -> {
                        if (result != null && result.length() > 0) {
                            ActionContext actionContext = getRealActionContext(treeNodeInfo);
                            if (actionContext.getAttr(result.toString()) != null) {
                                Toast.makeText(context, R.string.attribute_add_error, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            PinObject value = new PinString();
                            actionContext.addAttr(result.toString(), value);
                            actionContext.save();

                            TreeNode parentTreeNode;
                            if (subType == TreeNodeType.ATTR) {
                                parentTreeNode = attrTreeNode;
                            } else {
                                parentTreeNode = functionAttrTreeNode;
                            }

                            TreeNodeInfo info = new TreeNodeInfo(subType, result.toString(), value);
                            TreeNode node = new TreeNode(info, R.layout.view_card_list_attr_item);
                            parentTreeNode.addChild(node);
                            if (parentTreeNode.isExpanded()) manager.collapseNode(parentTreeNode);
                            manager.expandNode(parentTreeNode);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        public ViewHolder(@NonNull ViewCardListItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(Math.round(DisplayUtils.dp2px(context, 8)));

            binding.removeButton.setVisibility(View.VISIBLE);
            binding.removeButton.setOnClickListener(v -> AppUtils.showDialog(context, R.string.delete_function_tips, result -> {
                if (result) {
                    int index = getBindingAdapterPosition();
                    TreeNode treeNode = manager.get(index);
                    TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();

                    if (info.getType() == TreeNodeType.COMMON_FUNCTION) {
                        commonFunctionTreeNode.getChildren().remove(treeNode);
                        TaskRepository.getInstance().removeFunction(info.getKey());
                    } else {
                        functionTreeNode.getChildren().remove(treeNode);
                        ActionContext actionContext = getRealActionContext(info);
                        if (actionContext instanceof TaskContext) {
                            TaskContext taskContext = (TaskContext) actionContext;
                            taskContext.removeFunction(info.getKey());
                            taskContext.save();
                        }
                    }
                    manager.removeNode(treeNode);
                    notifyItemRemoved(index);
                }
            }));

            binding.copyButton.setVisibility(View.VISIBLE);
            binding.copyButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();

                TreeNode parentTreeNode;
                BaseFunction copy = null;
                if (info.getType() == TreeNodeType.COMMON_FUNCTION) {
                    BaseFunction function = TaskRepository.getInstance().getFunctionById(info.getKey());
                    copy = (BaseFunction) function.copy();
                    copy.setFunctionId(UUID.randomUUID().toString());
                    copy.save();
                    parentTreeNode = commonFunctionTreeNode;
                } else {
                    ActionContext actionContext = getRealActionContext(info);
                    if (actionContext instanceof TaskContext) {
                        TaskContext taskContext = (TaskContext) actionContext;
                        BaseFunction function = taskContext.getFunctionById(info.getKey());
                        copy = (BaseFunction) function.copy();
                        copy.setFunctionId(UUID.randomUUID().toString());
                        taskContext.addFunction(copy);
                        taskContext.save();
                    }
                    parentTreeNode = functionTreeNode;
                }

                if (copy == null) return;
                TreeNodeInfo copyInfo = new TreeNodeInfo(info.getType(), copy.getFunctionId(), copy.getTitle(context));
                TreeNode node = new TreeNode(copyInfo, R.layout.view_card_list_item);
                parentTreeNode.addChild(node);
                if (parentTreeNode.isExpanded()) manager.collapseNode(parentTreeNode);
                manager.expandNode(parentTreeNode);
                notifyDataSetChanged();
            });

            binding.exportButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();

                if (info.getType() == TreeNodeType.COMMON_FUNCTION) {
                    BaseFunction function = TaskRepository.getInstance().getFunctionById(info.getKey());
                    AppUtils.exportActionContexts(context, new ArrayList<>(Collections.singletonList(function)));
                }
            });

            binding.editButton.setVisibility(View.VISIBLE);
            binding.editButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();

                if (info.getType() == TreeNodeType.COMMON_FUNCTION) {
                    BaseFunction function = TaskRepository.getInstance().getFunctionById(info.getKey());
                    blueprintView.pushActionContext(function);
                } else {
                    ActionContext actionContext = getRealActionContext(info);
                    if (actionContext instanceof TaskContext) {
                        TaskContext taskContext = (TaskContext) actionContext;
                        BaseFunction function = taskContext.getFunctionById(info.getKey());
                        blueprintView.pushActionContext(function);
                    }
                }
                blueprintView.dismissDialog();
            });
        }

        public ViewHolder(@NonNull ViewCardListAttrItemBinding binding) {
            super(binding.getRoot());
            attrBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(Math.round(DisplayUtils.dp2px(context, 8)));

            binding.removeButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();

                ActionContext actionContext = getRealActionContext(info);
                ArrayList<BaseAction> actions = getValueActions(actionContext, info.getKey());
                if (actions.size() > 0) {
                    AppUtils.showDialog(context, R.string.delete_attribute_tips, result -> {
                        if (result) {
                            for (BaseAction action : actions) {
                                cardLayoutView.removeAction(action);
                            }
                            removeAttribute(actionContext);
                        }
                    });
                } else {
                    removeAttribute(actionContext);
                }
            });

            binding.getAttrButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                cardLayoutView.addAction(GetValueAction.class, info.getKey(), info.getValue());
            });

            binding.setAttrButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                cardLayoutView.addAction(SetValueAction.class, info.getKey(), info.getValue());
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
                    TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                    if (aClass.equals(info.getValue().getClass())) return;

                    try {
                        ActionContext actionContext = getRealActionContext(info);
                        PinObject pinObject = aClass.newInstance();
                        info.setValue(pinObject);
                        actionContext.addAttr(info.getKey(), pinObject);
                        for (BaseAction action : getValueActions(actionContext, info.getKey())) {
                            if (action instanceof SetValueAction) ((SetValueAction) action).setValue(pinObject.copy());
                            if (action instanceof GetValueAction) ((GetValueAction) action).setValue(pinObject.copy());
                            cardLayoutView.refreshActionPins(action);
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

        private ArrayList<BaseAction> getValueActions(ActionContext actionContext, String key) {
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

        private void removeAttribute(ActionContext actionContext) {
            int index = getBindingAdapterPosition();
            TreeNode treeNode = manager.get(index);
            TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();

            manager.removeNode(treeNode);
            if (info.getType() == TreeNodeType.ATTR) {
                attrTreeNode.getChildren().remove(treeNode);
            } else {
                functionAttrTreeNode.getChildren().remove(treeNode);
            }
            notifyItemRemoved(index);

            actionContext.removeAttr(info.getKey());
            actionContext.save();
        }

        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            TreeNodeInfo info = (TreeNodeInfo) node.getValue();
            if (level == 0) {
                typeBinding.title.setText(info.getTitle());
                typeBinding.recordButton.setVisibility(info.getSubType() == TreeNodeType.FUNCTION ? View.VISIBLE : View.GONE);
            } else if (level == 1) {
                if (info.getType() == TreeNodeType.COMMON_FUNCTION || info.getType() == TreeNodeType.FUNCTION) {
                    itemBinding.title.setText(info.getTitle());
                    itemBinding.exportButton.setVisibility(info.getType() == TreeNodeType.COMMON_FUNCTION ? View.VISIBLE : View.GONE);
                } else {
                    attrBinding.title.setText(info.getKey());
                    attrBinding.spinner.setSelection(map.indexOfKey(info.getValue().getClass()));
                }
            }
        }
    }

    private enum TreeNodeType {
        TYPE, COMMON_FUNCTION, FUNCTION, ATTR, FUNCTION_ATTR
    }

    private static class TreeNodeInfo {
        // 类型
        private final TreeNodeType type;
        // 变量名或方法id
        private String key;
        // 变量对象
        private PinObject value;
        // 方法名
        private String title;
        // 当前是哪个分类
        private TreeNodeType subType;

        public TreeNodeInfo(TreeNodeType type, String key, PinObject value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        public TreeNodeInfo(TreeNodeType type, String key, String title) {
            this.type = type;
            this.key = key;
            this.title = title;
        }

        public TreeNodeInfo(TreeNodeType subType, String title) {
            type = TreeNodeType.TYPE;
            this.subType = subType;
            this.title = title;
        }

        public TreeNodeType getType() {
            return type;
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

        public TreeNodeType getSubType() {
            return subType;
        }
    }
}
