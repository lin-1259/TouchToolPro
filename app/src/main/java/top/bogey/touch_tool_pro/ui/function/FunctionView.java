package top.bogey.touch_tool_pro.ui.function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.base.FunctionSaveChangedListener;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.base.TaskSaveChangedListener;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.ViewFunctionBinding;
import top.bogey.touch_tool_pro.ui.custom.CreateFunctionContextDialogBuilder;
import top.bogey.touch_tool_pro.ui.setting.HandleFunctionContextView;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class FunctionView extends Fragment implements TaskSaveChangedListener, FunctionSaveChangedListener {
    private ViewFunctionBinding binding;
    private FunctionListRecyclerViewAdapter adapter;

    private final LinkedHashMap<String, String> taskTitleMap = new LinkedHashMap<>();
    final HashMap<String, Function> selectedFunctions = new HashMap<>();
    Task task;
    boolean isSelect = false;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            unSelectAll();
            hideBottomBar();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SaveRepository.getInstance().removeTaskListener(this);
        SaveRepository.getInstance().removeFunctionListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewFunctionBinding.inflate(inflater, container, false);
        SaveRepository.getInstance().addTaskListener(this);
        SaveRepository.getInstance().addFunctionListener(this);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        taskTitleMap.put(getString(R.string.common_package_name), getString(R.string.common_name));
        taskTitleMap.putAll(SaveRepository.getInstance().getAllTasksTitle());
        taskTitleMap.forEach((taskId, title) -> binding.functionTabBox.addTab(binding.functionTabBox.newTab().setText(title)));

        binding.functionTabBox.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                selectTab(tab);
            }
        });

        adapter = new FunctionListRecyclerViewAdapter(this);
        binding.functionsBox.setAdapter(adapter);
        new TabLayoutMediator(binding.tabBox, binding.functionsBox, (tab, position) -> tab.setText(adapter.getTagByIndex(position))).attach();
        binding.functionTabBox.selectTab(binding.functionTabBox.getTabAt(0));

        binding.selectAllButton.setOnClickListener(v -> selectAll());

        binding.deleteButton.setOnClickListener(v -> AppUtils.showDialog(getContext(), R.string.delete_function_tips, result -> {
            if (result) {
                deleteSelectFunctions();
                hideBottomBar();
            }
        }));

        binding.exportButton.setOnClickListener(v -> {
            exportSelectTasks();
            hideBottomBar();
        });

        binding.moveButton.setOnClickListener(v -> showTagView());

        binding.copyButton.setOnClickListener(v -> {
            selectedFunctions.forEach((id, function) -> {
                Function copy = (Function) function.copy();
                copy.newInfo();
                copy.setTitle(getString(R.string.task_copy_title, copy.getTitle()));
                copy.save();
            });

            unSelectAll();
            hideBottomBar();
        });

        binding.cancelButton.setOnClickListener(v -> {
            unSelectAll();
            hideBottomBar();
        });

        binding.folderButton.setOnClickListener(v -> showTagView());

        binding.addButton.setOnClickListener(v -> {
            String tag = null;
            TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
            if (tab != null && tab.getText() != null) {
                tag = tab.getText().toString();
                if (SaveRepository.NO_TAG.equals(tag)) tag = null;
            }
            CreateFunctionContextDialogBuilder dialog = new CreateFunctionContextDialogBuilder(requireContext(), SaveRepository.getInstance().getFunctionTags(), tag);
            dialog.setTitle(R.string.function_add);
            dialog.setCallback(result -> {
                if (result && !dialog.getTitle().isEmpty()) {
                    Function function = new Function();
                    function.setTitle(dialog.getTitle());
                    function.addTags(dialog.getTags());
                    if (task != null) task.addFunction(function);
                    function.save();
                }
            });
            dialog.show();
        });

        return binding.getRoot();
    }

    private void selectTab(TabLayout.Tab tab) {
        if (tab.getText() == null) return;
        for (Map.Entry<String, String> entry : taskTitleMap.entrySet()) {
            if (entry.getValue().equals(tab.getText().toString())) {
                if (entry.getValue().equals(getString(R.string.common_name))) {
                    task = null;
                } else {
                    task = SaveRepository.getInstance().getTaskById(entry.getKey());
                }
                reCalculateTags();
                break;
            }
        }
    }

    public void reCalculateTags() {
        ArrayList<String> tags;
        if (task == null) {
            tags = SaveRepository.getInstance().getAllFunctionTags();
        } else {
            tags = task.getAllFunctionTags();
        }
        adapter.setTags(tags);
    }

    public void showBottomBar() {
        binding.exportButton.setVisibility(task == null ? View.VISIBLE : View.GONE);
        MainApplication.getInstance().getMainActivity().hideBottomNavigation();

        binding.addButton.hide();
        binding.bottomBar.setVisibility(View.VISIBLE);

        isSelect = true;
        callback.setEnabled(true);
    }

    public void hideBottomBar() {
        MainApplication.getInstance().getMainActivity().showBottomNavigation();

        binding.addButton.show();
        binding.bottomBar.setVisibility(View.GONE);

        isSelect = false;
        callback.setEnabled(false);
    }

    private void selectAll() {
        TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
        if (tab == null || tab.getText() == null) return;

        String tag = tab.getText().toString();
        ArrayList<Function> functions;
        if (task == null) {
            functions = SaveRepository.getInstance().getFunctionsByTag(tag);
        } else {
            functions = task.getFunctionsByTag(tag);
        }

        boolean flag = true;
        if (selectedFunctions.size() == functions.size()) {
            boolean matched = true;
            for (Function function : functions) {
                if (!selectedFunctions.containsKey(function.getId())) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                unSelectAll();
                flag = false;
            }
        }
        if (flag) {
            selectedFunctions.clear();
            functions.forEach(function -> selectedFunctions.put(function.getId(), function));
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
    }

    public void unSelectAll() {
        selectedFunctions.clear();
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    private void deleteSelectFunctions() {
        selectedFunctions.forEach((id, function) -> {
            if (task == null) {
                SaveRepository.getInstance().removeFunction(id);
            } else {
                task.removeFunction(function);
                task.save();
            }
        });
        unSelectAll();
    }

    private void showTagView() {
        FunctionTagView functionTagView = new FunctionTagView(this);
        functionTagView.show(requireActivity().getSupportFragmentManager(), null);
    }

    private void exportSelectTasks() {
        if (selectedFunctions.size() == 0) return;
        HandleFunctionContextView view = new HandleFunctionContextView(requireContext(), new ArrayList<>(selectedFunctions.values()));
        if (view.isEmpty()) return;
        view.switchState(true);

        new MaterialAlertDialogBuilder(requireContext())
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    AppUtils.exportFunctionContexts(requireContext(), view.getSelectActionContext());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setView(view)
                .setTitle(R.string.task_setting_export)
                .show();

        unSelectAll();
    }

    @Override
    public void onCreated(Function value) {
        reCalculateTags();
    }

    @Override
    public void onChanged(Function value) {
        reCalculateTags();
    }

    @Override
    public void onRemoved(Function value) {
        reCalculateTags();
    }

    @Override
    public void onCreated(Task value) {
        reCalculateTags();
    }

    @Override
    public void onChanged(Task value) {
        reCalculateTags();
    }

    @Override
    public void onRemoved(Task value) {
        reCalculateTags();
    }
}
