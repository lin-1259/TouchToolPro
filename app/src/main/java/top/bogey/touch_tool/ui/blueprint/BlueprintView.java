package top.bogey.touch_tool.ui.blueprint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Stack;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.ViewBlueprintBinding;
import top.bogey.touch_tool.ui.FragmentNavigateInterface;
import top.bogey.touch_tool.ui.MainActivity;

public class BlueprintView extends Fragment implements FragmentNavigateInterface {
    private ViewBlueprintBinding binding;
    private final Stack<ActionContext> actionContextStack = new Stack<>();
    private ActionSideSheetDialog dialog;

    @Override
    public boolean onBack() {
        if (actionContextStack.size() > 1) {
            popActionContext();
            return true;
        }
        return false;
    }

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

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.saveTask) {
                    binding.cardLayout.getActionContext().save();
                    return true;
                } else if (itemId == R.id.showLog) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.task_running_log)
                            .setMessage(TaskRepository.getInstance().getLogs(getContext(), task))
                            .setPositiveButton(R.string.close, (dialog, which) -> dialog.dismiss())
                            .setNegativeButton(R.string.task_running_log_clear, (dialog, which) -> {
                                dialog.dismiss();
                                TaskRepository.getInstance().removeLog(task);
                            })
                            .show();
                    return true;
                }
                return false;
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

        return binding.getRoot();
    }

    public void pushActionContext(ActionContext actionContext) {
        if (actionContext == null) return;

        if (actionContextStack.size() > 0) {
            actionContextStack.peek().save();
        }
        // 不能存在相同栈内元素
        actionContextStack.remove(actionContext);
        actionContextStack.push(actionContext);
        binding.cardLayout.setActionContext(actionContext);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (actionContext instanceof Task) actionBar.setSubtitle(((Task) actionContext).getTitle());
            else if (actionContext instanceof BaseFunction) actionBar.setSubtitle(((BaseFunction) actionContext).getTitle(getContext()));
        }
    }

    public void popActionContext() {
        ActionContext actionContext = actionContextStack.pop();
        actionContext.save();

        if (actionContextStack.size() > 0) {
            actionContext = actionContextStack.pop();
            pushActionContext(actionContext);
        }
    }

    public void dismissDialog() {
        if (dialog != null) dialog.dismiss();
    }

    @Override
    public void onDestroy() {
        try {
            binding.cardLayout.getActionContext().save();
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setSubtitle(null);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    public static void tryPushActionContext(ActionContext actionContext) {
        MainActivity activity = MainApplication.getInstance().getMainActivity();
        Fragment navFragment = activity.getSupportFragmentManager().getPrimaryNavigationFragment();
        if (navFragment == null || !navFragment.isAdded()) return;
        Fragment fragment = navFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof BlueprintView) {
            ((BlueprintView) fragment).pushActionContext(actionContext);
        }
    }
}
