package top.bogey.touch_tool.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.databinding.ActivityHomeTaskItemBinding;
import top.bogey.touch_tool.ui.blueprint.TaskBlueprintActivity;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.TaskChangedCallback;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> implements TaskChangedCallback, TaskRunningCallback {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final HomeActivity parent;
    private final String ALL;
    private final String NO;

    private boolean isCheck = false;
    private String tag;

    private final Map<String, Task> selectTasks = new HashMap<>();

    public TaskRecyclerViewAdapter(HomeActivity parent) {
        this.parent = parent;
        ALL = parent.getString(R.string.tag_all);
        NO = parent.getString(R.string.tag_no);

        showTasksByTag(ALL);

        MainAccessibilityService.serviceEnabled.observe(parent, aBoolean -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null) return;
            if (aBoolean) {
                service.addRunningCallback(this);
            } else {
                service.removeRunningCallback(this);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        TaskRepository.getInstance().addCallback(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        TaskRepository.getInstance().removeCallback(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ActivityHomeTaskItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.refreshItem(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public void onCreated(Task task) {
        if (ALL.equals(tag) || (task.getTag() != null && task.getTag().equals(tag)) || (task.getTag() == null && NO.equals(tag))) {
            tasks.add(task);
            notifyItemInserted(tasks.size());
        }
    }

    @Override
    public void onChanged(Task task) {
        int index = getTaskIndex(task);
        if (index >= 0) {
            tasks.set(index, task);
            notifyItemChanged(index);
        }
    }

    @Override
    public void onRemoved(Task task) {
        int index = getTaskIndex(task);
        if (index >= 0) {
            tasks.remove(index);
            notifyItemRemoved(index);
        }
    }

    @Override
    public void onStart(TaskRunnable runnable) {
        parent.getRoot().post(() -> onChanged(runnable.getTask()));
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
        parent.getRoot().post(() -> onChanged(runnable.getTask()));
    }

    @Override
    public void onProgress(TaskRunnable runnable, int progress) {
    }

    private int getTaskIndex(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            if (task.getId().equals(t.getId())) return i;
        }
        return -1;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void showTasksByTag(String tag) {
        this.tag = tag;
        ArrayList<Task> newTasks;
        if (ALL.equals(tag)) newTasks = new ArrayList<>(TaskRepository.getInstance().getAllTasks());
        else if (NO.equals(tag)) newTasks = TaskRepository.getInstance().getTasksByTag(null);
        else newTasks = TaskRepository.getInstance().getTasksByTag(tag);

        if (newTasks == null || newTasks.size() == 0) {
            int size = tasks.size();
            tasks.clear();
            notifyItemRangeRemoved(0, size);
            return;
        }

        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            boolean flag = true;
            for (Task newTask : newTasks) {
                if (task.getId().equals(newTask.getId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                tasks.remove(i);
                notifyItemRemoved(i);
            }
        }

        for (int i = 0; i < newTasks.size(); i++) {
            Task newTask = newTasks.get(i);
            boolean flag = true;
            for (Task task : tasks) {
                if (task.getId().equals(newTask.getId())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                tasks.add(i, newTask);
                notifyItemInserted(i);
            }
        }
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public void selectAll() {
        if (selectTasks.size() == tasks.size()) {
            selectTasks.clear();
            notifyItemRangeChanged(0, tasks.size());
            return;
        }
        selectTasks.clear();
        for (Task task : tasks) {
            selectTasks.put(task.getId(), task);
        }
        notifyItemRangeChanged(0, tasks.size());
    }

    public void unSelectAll() {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (selectTasks.containsKey(task.getId())) {
                selectTasks.remove(task.getId());
                notifyItemChanged(i);
            }
        }
        selectTasks.clear();
    }

    public void deleteSelectTasks() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            if (selectTasks.containsKey(task.getId())) {
                TaskRepository.getInstance().removeTask(task.getId());
            }
        }
        selectTasks.clear();
    }

    public void setSelectTasksTag(String tag) {
        selectTasks.forEach((id, task) -> {
            task.setTag(ALL.equals(tag) || NO.equals(tag) ? null : tag);
            TaskRepository.getInstance().saveTask(task);
        });
        unSelectAll();
    }

    public void exportSelectTasks() {
        if (selectTasks.size() == 0) return;
        Context context = parent;
        String name = context.getString(R.string.app_name);
        ArrayList<Task> tasks = new ArrayList<>(selectTasks.values());
        if (tasks.size() == 1) {
            name = tasks.get(0).getTitle();
        }

        String fileName = String.format("%s_%s %s.ttp", name, AppUtils.formatDateLocalDate(context, System.currentTimeMillis()), AppUtils.formatDateLocalTime(context, System.currentTimeMillis()));

        try (FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            String json = TaskRepository.getInstance().getGson().toJson(tasks);
            fileOutputStream.write(json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        File file = new File(context.getFilesDir(), fileName);
        Uri fileUri = null;
        try {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
        } catch (IllegalArgumentException ignored) {
        }
        if (fileUri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            String type = context.getContentResolver().getType(fileUri);
            intent.setType(type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_task_tips)));
        }
        unSelectAll();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final ActivityHomeTaskItemBinding binding;
        private final Context context;

        public ViewHolder(ActivityHomeTaskItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);
                if (isCheck) {
                    if (selectTasks.containsKey(task.getId())) {
                        selectTasks.remove(task.getId());
                    } else {
                        selectTasks.put(task.getId(), task);
                    }
                    notifyItemChanged(index);
                } else {
                    if (!AppUtils.isDebug(context)) {
                        MainAccessibilityService service = MainApplication.getInstance().getService();
                        if (service == null || !service.isServiceConnected()) {
                            Toast.makeText(context, R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Intent intent = new Intent(parent, TaskBlueprintActivity.class);
                    intent.putExtra("taskId", task.getId());
                    parent.startActivity(intent);
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                if (!isCheck) {
                    int index = getBindingAdapterPosition();
                    Task task = tasks.get(index);
                    selectTasks.put(task.getId(), task);
                    notifyItemChanged(index);
                    parent.showBottomBar();
                }
                return true;
            });

            binding.editButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);

                AppUtils.showEditDialog(context, R.string.task_change_title, task.getTitle(), result -> {
                    if (result != null && result.length() > 0) {
                        task.setTitle(result.toString());
                        binding.taskName.setText(result);
                        TaskRepository.getInstance().saveTask(task);
                    }
                });
            });

            binding.enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);

                if (isChecked == isAllStartActionEnable(task)) return;
                for (StartAction startAction : task.getStartActions(StartAction.class)) {
                    startAction.setEnable(isChecked);
                }
                TaskRepository.getInstance().saveTask(task);
            });

            binding.stopButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);
                MainAccessibilityService service = MainApplication.getInstance().getService();
                if (service != null) service.stopTask(task);
            });
        }

        public void refreshItem(Task task) {
            binding.taskName.setText(task.getTitle());
            binding.taskDes.setText(task.getTaskDes(context));
            binding.timeText.setText(AppUtils.formatDateLocalDate(context, task.getCreateTime()));
            binding.taskTag.setText(task.getTag());
            binding.enableSwitch.setChecked(isAllStartActionEnable(task));

            MainAccessibilityService service = MainApplication.getInstance().getService();
            binding.stopButton.setVisibility(service != null && service.isTaskRunning(task) ? View.VISIBLE : View.GONE);

            binding.getRoot().setChecked(selectTasks.containsKey(task.getId()));
        }

        private boolean isAllStartActionEnable(Task task) {
            for (StartAction startAction : task.getStartActions(StartAction.class)) {
                if (!startAction.isEnable()) return false;
            }
            return true;
        }
    }
}