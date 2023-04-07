package top.bogey.touch_tool.ui.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
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
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ViewTaskBinding;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.utils.AppUtils;

public class TaskView extends Fragment {
    private ViewTaskBinding binding;
    private TaskListRecyclerViewAdapter adapter;
    private String NO;

    public final Map<String, Task> selectTasks = new HashMap<>();
    public boolean isSelect = false;

    private final OnBackPressedCallback callback = new OnBackPressedCallback(false) {
        @Override
        public void handleOnBackPressed() {
            unSelectAll();
            hideBottomBar();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewTaskBinding.inflate(inflater, container, false);
        NO = getString(R.string.tag_no);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_task, menu);
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.importTask) {
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.launcherContent((code, intent) -> {
                        if (code == Activity.RESULT_OK) {
                            Uri uri = intent.getData();
                            if (uri != null) {
                                mainActivity.saveTasks(uri);
                            }
                        }
                    });
                }
                return true;
            }
        }, getViewLifecycleOwner());

        adapter = new TaskListRecyclerViewAdapter(this, TaskRepository.getInstance().getTags(requireContext()));
        binding.tasksBox.setAdapter(adapter);
        new TabLayoutMediator(binding.tabBox, binding.tasksBox, (tab, position) -> tab.setText(adapter.getTagByIndex(position))).attach();

        binding.selectAllButton.setOnClickListener(v -> selectAll());

        binding.deleteButton.setOnClickListener(v -> AppUtils.showDialog(getContext(), R.string.delete_task_tips, result -> {
            if (result) {
                deleteSelectTasks();
                hideBottomBar();
            }
        }));

        binding.exportButton.setOnClickListener(v -> {
            exportSelectTasks();
            hideBottomBar();
        });

        binding.folderButton.setOnClickListener(v -> showTagView());
        binding.moveButton.setOnClickListener(v -> showTagView());

        binding.cancelButton.setOnClickListener(v -> {
            unSelectAll();
            hideBottomBar();
        });

        binding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(getContext(), R.string.task_add, null, result -> {
            if (result != null && result.length() > 0) {
                Task task = new Task();
                task.setTitle(result.toString());
                TabLayout.Tab tab = binding.tabBox.getTabAt(binding.tabBox.getSelectedTabPosition());
                if (tab != null && tab.getText() != null) {
                    String tag = tab.getText().toString();
                    if (!NO.equals(tag)) task.setTag(tag);
                }
                task.save();
            }
        }));

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        return binding.getRoot();
    }


    public void showBottomBar() {
        ((MainActivity) requireActivity()).hideBottomNavigation();

        binding.addButton.hide();
        binding.bottomBar.setVisibility(View.VISIBLE);

        isSelect = true;

        callback.setEnabled(true);
    }

    public void hideBottomBar() {
        ((MainActivity) requireActivity()).showBottomNavigation();

        binding.addButton.show();
        binding.bottomBar.setVisibility(View.GONE);

        isSelect = false;

        callback.setEnabled(false);
    }

    private void selectAll() {
        ArrayList<Task> tasks = TaskRepository.getInstance().getAllTasks();
        if (selectTasks.size() == tasks.size()) {
            unSelectAll();
        } else {
            tasks.forEach(task -> selectTasks.put(task.getId(), task));
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
    }

    private void unSelectAll() {
        selectTasks.clear();
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
    }

    private void deleteSelectTasks() {
        selectTasks.forEach((id, task) -> TaskRepository.getInstance().removeTask(id));
        unSelectAll();
    }

    public void addTag(String tag) {
        if (tag == null) return;
        adapter.addTag(tag);
    }

    public void removeTag(String tag) {
        if (tag == null) return;
        adapter.removeTag(tag);
    }

    private void showTagView() {
        TagView tagView = new TagView(this);
        tagView.show(requireActivity().getSupportFragmentManager(), null);
    }

    public void selectTag(int index) {
        TabLayout.Tab tab = binding.tabBox.getTabAt(index);
        if (tab != null) {
            if (isSelect) {
                selectTasks.forEach((id, task) -> {
                    if (index == binding.tabBox.getTabCount() - 1) {
                        task.setTag(null);
                    } else {
                        task.setTag(String.valueOf(tab.getText()));
                    }
                    task.save();
                });
                unSelectAll();
                hideBottomBar();
            }
            binding.tabBox.selectTab(tab);
        }
    }

    private void exportSelectTasks() {
        if (selectTasks.size() == 0) return;
        AppUtils.exportActionContexts(requireContext(), new ArrayList<>(selectTasks.values()));
        unSelectAll();
    }
}
