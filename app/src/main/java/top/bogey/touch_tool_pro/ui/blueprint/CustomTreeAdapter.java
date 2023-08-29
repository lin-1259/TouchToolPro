package top.bogey.touch_tool_pro.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Space;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeNodeManager;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolder;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.var.GetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.GetVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetCommonVariableValue;
import top.bogey.touch_tool_pro.bean.action.var.SetVariableValue;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.ViewCardListAttrItemBinding;
import top.bogey.touch_tool_pro.databinding.ViewCardListItemBinding;
import top.bogey.touch_tool_pro.databinding.ViewCardListSubtypeItemBinding;
import top.bogey.touch_tool_pro.databinding.ViewCardListTypeItemBinding;
import top.bogey.touch_tool_pro.ui.recorder.RecorderFloatView;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.SpinnerSelectedListener;

public class CustomTreeAdapter extends TreeViewAdapter {
    private final TreeNodeManager manager;
    private final BlueprintView blueprintView;
    private final CardLayoutView cardLayoutView;

    private final TreeNode commonFunctionTreeNode;
    private final TreeNode functionTreeNode;
    private final TreeNode commonAttrTreeNode;
    private final TreeNode attrTreeNode;
    private final TreeNode functionAttrTreeNode;

    private final ArrayList<PinType> pinTypes = new ArrayList<>();

    public CustomTreeAdapter(BlueprintView blueprintView, CardLayoutView cardLayoutView, TreeNodeManager manager) {
        super(null, manager);
        this.manager = manager;
        this.blueprintView = blueprintView;
        this.cardLayoutView = cardLayoutView;

        for (PinType pinType : PinType.values()) {
            if (pinType.canCustom()) pinTypes.add(pinType);
        }

        setTreeNodeClickListener((treeNode, view) -> {
            TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
            if (info.type == TreeNodeType.NODE) {
                if (info.subType == TreeNodeSubtype.COMMON_FUNCTION || info.subType == TreeNodeSubtype.FUNCTION) {
                    if (info.subType == TreeNodeSubtype.COMMON_FUNCTION) {
                        cardLayoutView.addAction(info.key);
                    } else {
                        FunctionContext functionContext = getRealActionContext(info);
                        cardLayoutView.addAction(SaveRepository.getInstance().getFunction(functionContext.getId(), info.key));
                    }
                } else {
                    if (info.subType == TreeNodeSubtype.COMMON_ATTR) {
                        cardLayoutView.addAction(GetCommonVariableValue.class, info.key, (PinValue) info.value);
                    } else {
                        cardLayoutView.addAction(GetVariableValue.class, info.key, (PinValue) info.value);
                    }
                }
            }
        });

        commonFunctionTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.TYPE, TreeNodeSubtype.COMMON_FUNCTION, blueprintView.getString(R.string.function_title_common)), R.layout.view_card_list_type_item);
        functionTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.TYPE, TreeNodeSubtype.FUNCTION, blueprintView.getString(R.string.function_title)), R.layout.view_card_list_type_item);
        commonAttrTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.TYPE, TreeNodeSubtype.COMMON_ATTR, blueprintView.getString(R.string.attribute_title_common)), R.layout.view_card_list_type_item);
        attrTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.TYPE, TreeNodeSubtype.ATTR, blueprintView.getString(R.string.attribute_title)), R.layout.view_card_list_type_item);
        functionAttrTreeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.TYPE, TreeNodeSubtype.FUNCTION_ATTR, blueprintView.getString(R.string.attribute_title_function)), R.layout.view_card_list_type_item);

        initRoot();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        cardLayoutView.getFunctionContext().save();
        super.onDetachedFromRecyclerView(recyclerView);
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
        } else if (layoutId == R.layout.view_card_list_subtype_item) {
            return new ViewHolder(ViewCardListSubtypeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ViewHolder(ViewCardListAttrItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    private void initFunctions(TreeNode rootNode, ArrayList<Function> functions, TreeNodeSubtype subtype) {
        HashMap<String, ArrayList<Function>> functionMap = new HashMap<>();
        ArrayList<String> allTags = new ArrayList<>();
        functions.forEach(function -> {
            HashSet<String> tags = function.getTags();
            if (tags == null || tags.isEmpty() || (tags.size() == 1 && tags.contains(SaveRepository.NO_TAG))) {
                ArrayList<Function> list = functionMap.computeIfAbsent(SaveRepository.NO_TAG, k -> new ArrayList<>());
                list.add(function);
            } else {
                for (String tag : tags) {
                    ArrayList<Function> list = functionMap.computeIfAbsent(tag, k -> new ArrayList<>());
                    list.add(function);
                    if (!allTags.contains(tag)) allTags.add(tag);
                }
            }
        });

        Collator collator = Collator.getInstance(Locale.CHINA);
        allTags.sort(collator::compare);

        allTags.forEach(tag -> {
            ArrayList<Function> list = functionMap.get(tag);
            if (list == null) return;
            list.sort((fun1, fun2) -> collator.compare(fun1.getTitle(), fun2.getTitle()));
            if (tag.equals(SaveRepository.NO_TAG)) return;
            // 带分类的
            TreeNode treeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.SUBTYPE, subtype, tag), R.layout.view_card_list_subtype_item);
            list.forEach(function -> {
                TreeNode node = new TreeNode(new TreeNodeInfo(TreeNodeType.NODE, subtype, function.getId(), function.getTitle()), R.layout.view_card_list_item);
                treeNode.addChild(node);
            });
            rootNode.addChild(treeNode);
        });

        // 未分类的
        ArrayList<Function> list = functionMap.get(SaveRepository.NO_TAG);
        if (list == null) return;
        list.sort((fun1, fun2) -> collator.compare(fun1.getTitle(), fun2.getTitle()));
        list.forEach(function -> {
            TreeNode node = new TreeNode(new TreeNodeInfo(TreeNodeType.NODE, subtype, function.getId(), function.getTitle()), R.layout.view_card_list_item);
            rootNode.addChild(node);
        });
    }

    private void initVariables(TreeNode rootNode, HashMap<String, PinValue> variables, TreeNodeSubtype subtype) {
        ArrayList<String> list = new ArrayList<>(variables.keySet());
        Collator collator = Collator.getInstance(Locale.CHINA);
        list.sort(collator::compare);
        list.forEach(key -> {
            PinValue variable = variables.get(key);
            if (variable == null) return;
            TreeNode treeNode = new TreeNode(new TreeNodeInfo(TreeNodeType.NODE, subtype, key, variable), R.layout.view_card_list_attr_item);
            rootNode.addChild(treeNode);
        });
    }

    public void initRoot() {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();

        // 通用自定义卡
        ArrayList<Function> functions = SaveRepository.getInstance().getAllFunctions();
        initFunctions(commonFunctionTreeNode, functions, TreeNodeSubtype.COMMON_FUNCTION);
        treeNodes.add(commonFunctionTreeNode);

        HashMap<String, PinValue> allVariables = SaveRepository.getInstance().getAllVariables();
        initVariables(commonAttrTreeNode, allVariables, TreeNodeSubtype.COMMON_ATTR);
        treeNodes.add(commonAttrTreeNode);

        FunctionContext functionContext = cardLayoutView.getFunctionContext();
        FunctionContext parent = functionContext.getParent();
        FunctionContext context = parent == null ? functionContext : parent;

        // 任务
        if (context instanceof Task task) {
            initFunctions(functionTreeNode, new ArrayList<>(task.getFunctions()), TreeNodeSubtype.FUNCTION);
            treeNodes.add(functionTreeNode);

            initVariables(attrTreeNode, task.getVars(), TreeNodeSubtype.ATTR);
            treeNodes.add(attrTreeNode);
        }

        // 自定义卡的变量
        if (functionContext instanceof Function) {
            initVariables(functionAttrTreeNode, functionContext.getVars(), TreeNodeSubtype.FUNCTION_ATTR);
            treeNodes.add(functionAttrTreeNode);
        }

        updateTreeNodes(treeNodes);
        expandNode(commonFunctionTreeNode);
        expandNode(functionTreeNode);
        expandNode(commonAttrTreeNode);
        expandNode(attrTreeNode);
        expandNode(functionAttrTreeNode);
    }

    public FunctionContext getRealActionContext(TreeNodeInfo info) {
        FunctionContext functionContext = cardLayoutView.getFunctionContext();
        if (info.subType == TreeNodeSubtype.FUNCTION_ATTR) return functionContext;

        FunctionContext parentContext = functionContext.getParent();
        if (parentContext != null) return parentContext;

        return functionContext;
    }

    protected class ViewHolder extends TreeViewHolder {
        private ViewCardListTypeItemBinding typeBinding;
        private ViewCardListSubtypeItemBinding subtypeBinding;
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
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                TreeNodeSubtype subType = info.subType;

                if (subType == TreeNodeSubtype.FUNCTION) {
                    FunctionContext functionContext = getRealActionContext(info);
                    if (functionContext instanceof Task task) {
                        AppUtils.showEditDialog(context, R.string.function_record, null, result -> {
                            if (result != null && result.length() > 0) {
                                Function function = new Function();
                                function.setTitle(result.toString());
                                new RecorderFloatView(context, () -> {
                                    task.addFunction(function);
                                    task.save();

                                    TreeNodeInfo nodeInfo = new TreeNodeInfo(TreeNodeType.NODE, subType, function.getId(), function.getTitle());
                                    TreeNode node = new TreeNode(nodeInfo, R.layout.view_card_list_item);
                                    functionTreeNode.addChild(node);
                                    if (functionTreeNode.isExpanded()) manager.collapseNode(functionTreeNode);
                                    manager.expandNode(functionTreeNode);
                                    notifyDataSetChanged();
                                }, function).show();
                            }
                        });
                    }
                }
            });

            typeBinding.addButton.setVisibility(View.VISIBLE);
            typeBinding.addButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                TreeNodeSubtype subType = info.subType;

                if (subType == TreeNodeSubtype.COMMON_FUNCTION || subType == TreeNodeSubtype.FUNCTION) {
                    AppUtils.showEditDialog(context, R.string.function_add, null, result -> {
                        if (result != null && result.length() > 0) {
                            Function function = new Function();
                            function.setTitle(result.toString());

                            TreeNode parentTreeNode;
                            if (subType == TreeNodeSubtype.COMMON_FUNCTION) {
                                parentTreeNode = commonFunctionTreeNode;
                            } else {
                                FunctionContext functionContext = getRealActionContext(info);
                                if (functionContext instanceof Task task) {
                                    task.addFunction(function);
                                }
                                parentTreeNode = functionTreeNode;
                            }
                            function.save();

                            TreeNodeInfo nodeInfo = new TreeNodeInfo(TreeNodeType.NODE, subType, function.getId(), function.getTitle());
                            TreeNode node = new TreeNode(nodeInfo, R.layout.view_card_list_item);
                            parentTreeNode.addChild(node);
                            if (parentTreeNode.isExpanded()) manager.collapseNode(parentTreeNode);
                            manager.expandNode(parentTreeNode);
                            notifyDataSetChanged();
                        }
                    });
                } else if (subType == TreeNodeSubtype.ATTR || subType == TreeNodeSubtype.FUNCTION_ATTR || subType == TreeNodeSubtype.COMMON_ATTR) {
                    AppUtils.showEditDialog(context, R.string.attribute_add, null, result -> {
                        if (result != null && result.length() > 0) {
                            PinString value = new PinString();
                            if (subType == TreeNodeSubtype.COMMON_ATTR) {
                                PinValue variable = SaveRepository.getInstance().getVariable(result.toString());
                                if (variable != null) {
                                    Toast.makeText(context, R.string.attribute_add_error, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SaveRepository.getInstance().addVariable(result.toString(), value);
                            } else {
                                FunctionContext functionContext = getRealActionContext(info);
                                if (functionContext.getVar(result.toString()) != null) {
                                    Toast.makeText(context, R.string.attribute_add_error, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                functionContext.addVar(result.toString(), value);
                                functionContext.save();
                            }

                            TreeNode parentTreeNode;
                            if (subType == TreeNodeSubtype.ATTR) {
                                parentTreeNode = attrTreeNode;
                            } else if (subType == TreeNodeSubtype.FUNCTION_ATTR) {
                                parentTreeNode = functionAttrTreeNode;
                            } else {
                                parentTreeNode = commonAttrTreeNode;
                            }

                            TreeNodeInfo nodeInfo = new TreeNodeInfo(TreeNodeType.NODE, subType, result.toString(), value);
                            TreeNode node = new TreeNode(nodeInfo, R.layout.view_card_list_attr_item);
                            parentTreeNode.addChild(node);
                            if (parentTreeNode.isExpanded()) manager.collapseNode(parentTreeNode);
                            manager.expandNode(parentTreeNode);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        public ViewHolder(@NonNull ViewCardListSubtypeItemBinding binding) {
            super(binding.getRoot());
            subtypeBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);
        }

        public ViewHolder(@NonNull ViewCardListItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            context = binding.getRoot().getContext();
            setNodePadding(0);

            binding.removeButton.setVisibility(View.VISIBLE);
            binding.removeButton.setOnClickListener(v -> AppUtils.showDialog(context, R.string.delete_function_tips, result -> {
                if (result) {
                    int index = getBindingAdapterPosition();
                    TreeNode treeNode = manager.get(index);
                    TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                    TreeNodeSubtype subType = info.subType;

                    if (subType == TreeNodeSubtype.COMMON_FUNCTION) {
                        commonFunctionTreeNode.getChildren().remove(treeNode);
                        SaveRepository.getInstance().removeFunction(info.key);
                    } else {
                        functionTreeNode.getChildren().remove(treeNode);
                        FunctionContext functionContext = getRealActionContext(info);
                        if (functionContext instanceof Task task) {
                            task.removeFunction(task.getFunctionById(info.key));
                            task.save();
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
                TreeNodeSubtype subType = info.subType;

                TreeNode parentTreeNode;
                Function copy = null;
                if (subType == TreeNodeSubtype.COMMON_FUNCTION) {
                    Function function = SaveRepository.getInstance().getFunctionById(info.key);
                    copy = (Function) function.copy();
                    copy.save();
                    parentTreeNode = commonFunctionTreeNode;
                } else {
                    FunctionContext functionContext = getRealActionContext(info);
                    if (functionContext instanceof Task task) {
                        Function function = task.getFunctionById(info.key);
                        copy = (Function) function.copy();
                        task.addFunction(copy);
                        task.save();
                    }
                    parentTreeNode = functionTreeNode;
                }
                if (copy == null) return;

                TreeNodeInfo nodeInfo = new TreeNodeInfo(TreeNodeType.NODE, subType, copy.getId(), copy.getTitle());
                TreeNode node = new TreeNode(nodeInfo, R.layout.view_card_list_item);
                parentTreeNode.addChild(node);
                if (parentTreeNode.isExpanded()) manager.collapseNode(parentTreeNode);
                manager.expandNode(parentTreeNode);
                notifyDataSetChanged();
            });

            binding.editButton.setVisibility(View.VISIBLE);
            binding.editButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                TreeNodeSubtype subType = info.subType;

                if (subType == TreeNodeSubtype.COMMON_FUNCTION) {
                    Function function = SaveRepository.getInstance().getFunctionById(info.key);
                    blueprintView.pushActionContext(function);
                } else {
                    FunctionContext functionContext = getRealActionContext(info);
                    if (functionContext instanceof Task task) {
                        Function function = task.getFunctionById(info.key);
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
            setNodePadding(0);

            binding.removeButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                TreeNodeSubtype subType = info.subType;

                manager.removeNode(treeNode);
                if (subType == TreeNodeSubtype.COMMON_ATTR) {
                    SaveRepository.getInstance().removeVariable(info.key);
                    commonAttrTreeNode.getChildren().remove(treeNode);
                } else {
                    FunctionContext functionContext = getRealActionContext(info);
                    functionContext.removeVar(info.key);
                    functionContext.save();
                    if (subType == TreeNodeSubtype.ATTR) {
                        attrTreeNode.getChildren().remove(treeNode);
                    } else {
                        functionAttrTreeNode.getChildren().remove(treeNode);
                    }
                }

                notifyItemRemoved(index);
            });

            binding.getAttrButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                if (info.subType == TreeNodeSubtype.COMMON_ATTR) {
                    cardLayoutView.addAction(GetCommonVariableValue.class, info.key, (PinValue) info.value);
                } else {
                    cardLayoutView.addAction(GetVariableValue.class, info.key, (PinValue) info.value);
                }
            });

            binding.setAttrButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                TreeNode treeNode = manager.get(index);
                TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                if (info.subType == TreeNodeSubtype.COMMON_ATTR) {
                    cardLayoutView.addAction(SetCommonVariableValue.class, info.key, (PinValue) info.value);
                } else {
                    cardLayoutView.addAction(SetVariableValue.class, info.key, (PinValue) info.value);
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
            pinTypes.forEach(pinType -> adapter.add(pinType.getTitle()));
            binding.spinner.setAdapter(adapter);
            binding.spinner.setOnItemSelectedListener(new SpinnerSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int index = getBindingAdapterPosition();
                    TreeNode treeNode = manager.get(index);
                    TreeNodeInfo info = (TreeNodeInfo) treeNode.getValue();
                    PinType pinType = pinTypes.get(position);
                    if (pinType == info.value.getType()) return;

                    try {
                        Class<? extends PinObject> aClass = pinType.getPinObjectClass();
                        PinValue pinValue = (PinValue) aClass.newInstance();
                        info.value = pinValue;

                        if (info.subType == TreeNodeSubtype.COMMON_ATTR) {
                            SaveRepository.getInstance().addVariable(info.key, pinValue);
                        } else {
                            FunctionContext functionContext = cardLayoutView.getFunctionContext().findVarParent(info.key);
                            if (functionContext == null) return;
                            functionContext.addVar(info.key, pinValue);
                            functionContext.save();

                            for (Action action : cardLayoutView.getFunctionContext().getActionsByClass(GetVariableValue.class)) {
                                GetVariableValue getValue = (GetVariableValue) action;
                                if (getValue.getVarKey().equals(info.key)) {
                                    getValue.setValue((PinValue) pinValue.copy());
                                    cardLayoutView.refreshVariableActionPins(getValue);
                                }
                            }

                            for (Action action : cardLayoutView.getFunctionContext().getActionsByClass(SetVariableValue.class)) {
                                SetVariableValue setValue = (SetVariableValue) action;
                                if (setValue.getVarKey().equals(info.key)) {
                                    setValue.setValue((PinValue) pinValue.copy());
                                    cardLayoutView.refreshVariableActionPins(setValue);
                                }
                            }
                        }
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        public void refreshItem(TreeNode node) {
            int level = node.getLevel();
            TreeNodeInfo info = (TreeNodeInfo) node.getValue();
            TreeNodeSubtype subType = info.subType;

            Space space = null;
            switch (info.type) {
                case TYPE -> {
                    typeBinding.title.setText(info.title);
                    typeBinding.recordButton.setVisibility(subType == TreeNodeSubtype.FUNCTION ? View.VISIBLE : View.GONE);
                }
                case SUBTYPE -> {
                    subtypeBinding.title.setText(info.title);
                    subtypeBinding.imageView.setImageResource(node.isExpanded() ? R.drawable.icon_up : R.drawable.icon_down);
                    space = subtypeBinding.space;
                }
                case NODE -> {
                    switch (subType) {
                        case COMMON_FUNCTION, FUNCTION -> {
                            itemBinding.title.setText(info.title);
                            space = itemBinding.space;
                        }
                        case COMMON_ATTR, ATTR, FUNCTION_ATTR -> {
                            attrBinding.title.setText(info.key);
                            int index = pinTypes.indexOf(info.value.getType());
                            attrBinding.spinner.setSelection(index);
                            space = attrBinding.space;

                            if (subType == TreeNodeSubtype.COMMON_ATTR) attrBinding.icon.setImageResource(R.drawable.icon_stop);
                            else if (subType == TreeNodeSubtype.ATTR) attrBinding.icon.setImageResource(R.drawable.icon_play);
                            else attrBinding.icon.setImageResource(0);
                        }
                    }
                }
            }

            if (space != null) {
                ViewGroup.LayoutParams params = space.getLayoutParams();
                params.width = (int) (DisplayUtils.dp2px(context, 8) * level);
                space.setLayoutParams(params);
            }
        }
    }

    private enum TreeNodeType {
        TYPE, SUBTYPE, NODE
    }

    private enum TreeNodeSubtype {
        COMMON_FUNCTION, FUNCTION, COMMON_ATTR, ATTR, FUNCTION_ATTR
    }

    private static class TreeNodeInfo {
        // 类型
        private final TreeNodeType type;
        private final TreeNodeSubtype subType;
        // 变量名或方法id
        private String key;
        // 变量对象
        private PinObject value;
        // 方法名
        private String title;


        public TreeNodeInfo(TreeNodeType type, TreeNodeSubtype subType, String key, PinObject value) {
            this.type = type;
            this.subType = subType;
            this.key = key;
            this.value = value;
        }

        public TreeNodeInfo(TreeNodeType type, TreeNodeSubtype subType, String key, String title) {
            this.type = type;
            this.subType = subType;
            this.key = key;
            this.title = title;
        }

        public TreeNodeInfo(TreeNodeType type, TreeNodeSubtype subType, String title) {
            this.type = type;
            this.subType = subType;
            this.title = title;
        }
    }
}
