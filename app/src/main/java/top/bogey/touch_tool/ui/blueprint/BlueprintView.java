package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Stack;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.ViewBlueprintBinding;

public class BlueprintView extends Fragment {
    private ViewBlueprintBinding binding;
    private final Stack<ActionContext> actionContextStack = new Stack<>();
    private ActionSideSheetDialog dialog;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            popActionContext();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getArguments() == null) throw new IllegalArgumentException();
        String taskId = getArguments().getString("taskId");
        Task task = TaskRepository.getInstance().getTaskById(taskId);
        if (task == null) throw new IllegalArgumentException();

        binding = ViewBlueprintBinding.inflate(inflater, container, false);
        pushActionContext(task);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_task_detail, menu);
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.saveTask:
                        binding.cardLayout.getActionContext().save();
                        break;
                    case R.id.showLog:
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.task_running_log)
                                .setMessage(TaskRepository.getInstance().getLogs(getContext(), task))
                                .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                                .setNegativeButton(R.string.task_running_log_clear, (dialog, which) -> {
                                    dialog.dismiss();
                                    TaskRepository.getInstance().removeLog(task);
                                })
                                .show();
                        break;
                }
                return true;
            }
        }, getViewLifecycleOwner());

        binding.addButton.setOnClickListener(v -> {
            ActionTreeAdapter adapter = new ActionTreeAdapter(binding.cardLayout, new TreeNodeManager());
            dialog = new ActionSideSheetDialog(requireContext(), adapter);
            dialog.show();
        });

        binding.attrButton.setOnClickListener(v -> {
            CustomTreeAdapter adapter = new CustomTreeAdapter(this, binding.cardLayout, new TreeNodeManager());
            dialog = new ActionSideSheetDialog(requireContext(), adapter);
            dialog.show();
        });

        binding.lockEditButton.setOnClickListener(v -> {
            boolean editMode = binding.cardLayout.isEditMode();
            binding.cardLayout.setEditMode(!editMode);
            binding.lockEditButton.setImageResource(editMode ? R.drawable.icon_hand : R.drawable.icon_edit);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return binding.getRoot();
    }

    public void pushActionContext(ActionContext actionContext) {
        // 不能存在相同栈内元素
        actionContextStack.remove(actionContext);
        actionContextStack.push(actionContext);
        binding.cardLayout.setActionContext(actionContext);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (actionContext instanceof Task) actionBar.setSubtitle(((Task) actionContext).getTitle());
            else if (actionContext instanceof BaseFunction) actionBar.setSubtitle(((BaseFunction) actionContext).getTitle(getContext()));
        }
        callback.setEnabled(actionContextStack.size() > 1);
    }

    public void popActionContext() {
        ActionContext actionContext = actionContextStack.pop();
        actionContext.save();

        if (actionContextStack.size() > 0) {
            actionContext = actionContextStack.pop();
            pushActionContext(actionContext);
        }
        callback.setEnabled(actionContextStack.size() > 1);
    }

    public void dismissDialog() {
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.cardLayout.getActionContext().save();
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setSubtitle(null);
    }
}
