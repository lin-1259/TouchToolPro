package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ActivityBlueprintBinding;
import top.bogey.touch_tool.ui.BaseActivity;

public class TaskBlueprintActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) throw new IllegalArgumentException();

        String taskId = intent.getStringExtra("taskId");
        Task task = TaskRepository.getInstance().getTaskById(taskId);

        ActivityBlueprintBinding binding = ActivityBlueprintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);
        addMenuProvider(new MenuProvider() {
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
                        new MaterialAlertDialogBuilder(TaskBlueprintActivity.this)
                                .setTitle(R.string.task_running_log)
                                .setMessage(TaskRepository.getInstance().getLogs(TaskBlueprintActivity.this, task))
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
        });

        binding.cardLayout.setActionContext(task);

        binding.addButton.setOnClickListener(v -> {
            ActionSideSheetDialog dialog = new ActionSideSheetDialog(this, binding.cardLayout);
            dialog.show();
        });

        binding.lockEditButton.setOnClickListener(v -> {
            boolean editMode = binding.cardLayout.isEditMode();
            binding.cardLayout.setEditMode(!editMode);
            binding.lockEditButton.setImageResource(editMode ? R.drawable.icon_hand : R.drawable.icon_edit);
        });

        binding.toolBar.setTitle(R.string.task_title);
        binding.toolBar.setSubtitle(task.getTitle());
    }
}
