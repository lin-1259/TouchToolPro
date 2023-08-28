package top.bogey.touch_tool_pro.ui.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.base.TaskSaveChangedListener;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.ViewTaskListItemBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> implements TaskSaveChangedListener {
    private final TaskView taskView;
    private final ArrayList<Task> tasks = new ArrayList<>();

    private String tag;

    public TaskRecyclerViewAdapter(TaskView taskView) {
        this.taskView = taskView;
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
        HashSet<String> tags = task.getTags();
        boolean emptyTag = tags == null || tags.isEmpty();
        // 标签不是空，就匹配当前页签，为空就看当前页签是不是No_Tag
        if ((!emptyTag && tags.contains(tag)) || (emptyTag && SaveRepository.NO_TAG.equals(tag))) {
            tasks.add(task);
            notifyItemInserted(tasks.size());
        }
    }

    @Override
    public void onChanged(Task task) {
        int index = tasks.indexOf(task);
        if (index >= 0) {
            tasks.set(index, task);
            notifyItemChanged(index);
        }
    }

    @Override
    public void onRemoved(Task task) {
        int index = tasks.indexOf(task);
        if (index >= 0) {
            tasks.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void showTasksByTag(String tag) {
        this.tag = tag;
        ArrayList<Task> newTasks = SaveRepository.getInstance().getTasksByTag(tag);
        tasks.clear();
        tasks.addAll(newTasks);
        Collator collator = Collator.getInstance(Locale.CHINA);
        tasks.sort((task1, task2) -> collator.compare(task1.getTitle(), task2.getTitle()));
        notifyDataSetChanged();
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
                    if (taskView.selectedTasks.containsKey(task.getId())) {
                        taskView.selectedTasks.remove(task.getId());
                    } else {
                        taskView.selectedTasks.put(task.getId(), task);
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
                    controller.navigate(TaskViewDirections.actionTaskToBlueprint(task.getId(), null));
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                int index = getBindingAdapterPosition();
                Task task = tasks.get(index);
                if (taskView.isSelect) {
                    if (taskView.selectedTasks.containsKey(task.getId())) {
                        taskView.selectedTasks.remove(task.getId());
                    } else {
                        taskView.selectedTasks.put(task.getId(), task);
                    }
                    notifyItemChanged(index);
                } else {
                    taskView.showBottomBar();
                    taskView.selectedTasks.put(task.getId(), task);
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

                if (isChecked == task.isEnable()) return;
                for (Action action : task.getActionsByClass(StartAction.class)) {
                    ((StartAction) action).setEnable(isChecked);
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

            String description = task.getDescription();
            binding.taskDes.setText(description);
            binding.taskDes.setVisibility(description.isEmpty() ? View.GONE : View.VISIBLE);

            binding.timeText.setText(AppUtils.formatDateLocalDate(context, task.getCreateTime()));

            String tagString = task.getTagString();
            binding.taskTag.setText(tagString);
            binding.taskTag.setVisibility(tagString.isEmpty() ? View.GONE : View.VISIBLE);

            binding.enableSwitch.setChecked(task.isEnable());

            MainAccessibilityService service = MainApplication.getInstance().getService();
            binding.stopButton.setVisibility(service != null && service.isTaskRunning(task) ? View.VISIBLE : View.GONE);

            ActionCheckResult result = task.check();
            if (result.type == ActionCheckResult.ActionResultType.ERROR) {
                binding.errorText.setVisibility(View.VISIBLE);
                binding.errorText.setText(result.tips);
            } else {
                binding.errorText.setVisibility(View.GONE);
            }

            binding.getRoot().setChecked(taskView.selectedTasks.containsKey(task.getId()));
        }
    }
}