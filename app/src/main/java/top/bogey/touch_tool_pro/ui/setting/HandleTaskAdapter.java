package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextItemBinding;

public class HandleTaskAdapter extends RecyclerView.Adapter<HandleTaskAdapter.ViewHolder> {
    private final HandleFunctionContextView handleView;
    private final ArrayList<Task> tasks;
    private final ArrayList<Task> selectedTasks = new ArrayList<>();

    public HandleTaskAdapter(HandleFunctionContextView handleView, ArrayList<Task> tasks) {
        this.handleView = handleView;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogHandleActionContextItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refreshItem(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public ArrayList<Task> getSelectedTasks() {
        return selectedTasks;
    }

    public void selectAll(boolean all) {
        selectedTasks.clear();
        if (all) {
            selectedTasks.addAll(tasks);
        }
        refreshCheckBox();
        notifyDataSetChanged();
    }

    public void selectNotExist() {
        SaveRepository repository = SaveRepository.getInstance();
        tasks.forEach(task -> {
            Task taskById = repository.getTaskById(task.getId());
            if (taskById == null) selectedTasks.add(task);
        });
        refreshCheckBox();
        notifyDataSetChanged();
    }

    public void refreshCheckBox() {
        if (tasks.isEmpty()) {
            handleView.setTaskCheck(-1);
        } else if (selectedTasks.isEmpty()) {
            handleView.setTaskCheck(MaterialCheckBox.STATE_UNCHECKED);
        } else if (selectedTasks.size() == tasks.size()) {
            handleView.setTaskCheck(MaterialCheckBox.STATE_CHECKED);
        } else {
            handleView.setTaskCheck(MaterialCheckBox.STATE_INDETERMINATE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final DialogHandleActionContextItemBinding binding;
        private final Context context;

        public ViewHolder(DialogHandleActionContextItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                binding.checkBox.toggle();
                selectActionContext(binding.checkBox.isChecked());
            });

            binding.checkBox.setOnClickListener(v -> selectActionContext(binding.checkBox.isChecked()));
        }

        private void selectActionContext(boolean select) {
            int index = getBindingAdapterPosition();
            Task task = tasks.get(index);
            if (select) {
                if (!selectedTasks.contains(task)) selectedTasks.add(task);
            } else {
                selectedTasks.remove(task);
            }
            refreshCheckBox();
        }

        public void refreshItem(Task task) {
            binding.nameTitle.setText(task.getTitle());
            binding.checkBox.setChecked(selectedTasks.contains(task));
        }
    }
}
