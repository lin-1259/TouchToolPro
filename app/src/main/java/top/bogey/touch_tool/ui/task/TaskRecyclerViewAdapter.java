package top.bogey.touch_tool.ui.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.databinding.ViewTaskListItemBinding;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.TaskChangedCallback;
import top.bogey.touch_tool.utils.TaskRunningCallback;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> implements TaskChangedCallback, TaskRunningCallback {
    private final String NO;

    private final TaskView taskView;
    private final ArrayList<Task> tasks = new ArrayList<>();

    private RecyclerView recyclerView;
    private String tag;

    public TaskRecyclerViewAdapter(TaskView taskView) {
        this.taskView = taskView;
        NO = taskView.getString(R.string.tag_no);

        MainAccessibilityService.serviceEnabled.observe(taskView.getViewLifecycleOwner(), aBoolean -> {
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
        this.recyclerView = recyclerView;
        TaskRepository.getInstance().addCallback(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
        TaskRepository.getInstance().removeCallback(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewTaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        if ((task.getTag() != null && task.getTag().equals(tag)) || (task.getTag() == null && NO.equals(tag))) {
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
        if (recyclerView == null) return;
        recyclerView.post(() -> onChanged(runnable.getTask()));
    }

    @Override
    public void onEnd(TaskRunnable runnable) {
        if (recyclerView == null) return;
        recyclerView.post(() -> onChanged(runnable.getTask()));
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

    public void showTasksByTag(String tag) {
        this.tag = tag;
        ArrayList<Task> newTasks;
        if (NO.equals(tag)) newTasks = TaskRepository.getInstance().getTasksByTag(null);
        else newTasks = TaskRepository.getInstance().getTasksByTag(tag);
        tasks.clear();
        if (newTasks != null) tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewTaskListItemBinding binding;
        private final Context context;

        public ViewHolder(ViewTaskListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);
                if (taskView.isSelect) {
                    if (taskView.selectTasks.containsKey(task.getId())) {
                        taskView.selectTasks.remove(task.getId());
                    } else {
                        taskView.selectTasks.put(task.getId(), task);
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

                    NavController controller = Navigation.findNavController(taskView.requireActivity(), R.id.conView);
                    controller.navigate(TaskViewDirections.actionTaskToBlueprintView(task.getId()));
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);
                if (taskView.isSelect) {
                    if (taskView.selectTasks.containsKey(task.getId())) {
                        taskView.selectTasks.remove(task.getId());
                    } else {
                        taskView.selectTasks.put(task.getId(), task);
                    }
                    notifyItemChanged(index);
                } else {
                    taskView.showBottomBar();
                    taskView.selectTasks.put(task.getId(), task);
                    notifyItemChanged(index);
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
                        task.save();
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
                task.save();
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

            binding.getRoot().setChecked(taskView.selectTasks.containsKey(task.getId()));
        }

        private boolean isAllStartActionEnable(Task task) {
            for (StartAction startAction : task.getStartActions(StartAction.class)) {
                if (!startAction.isEnable()) return false;
            }
            return true;
        }
    }
}