package top.bogey.touch_tool.ui.task_blueprint;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ViewTaskBlueprintBinding;

public class TaskBlueprintView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() == null) throw new IllegalArgumentException();

        ViewTaskBlueprintBinding binding = ViewTaskBlueprintBinding.inflate(inflater, container, false);

        String taskId = getArguments().getString("taskId");
        Task task = TaskRepository.getInstance().getTaskById(taskId);
        binding.cardLayout.setTask(task);

        binding.addButton.setOnClickListener(v -> {
            ActionSideSheetDialog dialog = new ActionSideSheetDialog(requireContext(), binding.cardLayout);
            dialog.show();
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_task, menu);
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.saveTask:
                        TaskRepository.getInstance().saveTask(task);
                        break;
                    case R.id.showLog:
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle(R.string.task_running_log)
                                .setMessage(TaskRepository.getInstance().getLogs(requireContext(), task))
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

        return binding.getRoot();
    }
}
