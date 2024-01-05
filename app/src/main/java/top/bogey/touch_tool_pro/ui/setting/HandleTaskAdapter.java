package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextItemBinding;

public class HandleTaskAdapter extends RecyclerView.Adapter<HandleTaskAdapter.ViewHolder> {
    private final HandleFunctionContextView handleView;
    private final HashMap<String, Task> tasks;
    private final ArrayList<String> keys;
    private final HashMap<String, Task> selectedTasks = new HashMap<>();
    private final HashMap<String, Task> requiredTasks = new HashMap<>();

    public HandleTaskAdapter(HandleFunctionContextView handleView, HashMap<String, Task> tasks) {
        this.handleView = handleView;
        this.tasks = tasks;
        keys = new ArrayList<>(tasks.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogHandleActionContextItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = keys.get(position);
        holder.refreshItem(Objects.requireNonNull(tasks.get(key)));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public HashMap<String, Task> getAllSelectedTasks() {
        HashMap<String, Task> hashMap = new HashMap<>();
        hashMap.putAll(requiredTasks);
        hashMap.putAll(selectedTasks);
        return hashMap;
    }

    public HashMap<String, Task> getSelectedTasks() {
        return selectedTasks;
    }

    public void selectAll(boolean all) {
        selectedTasks.clear();
        if (all) {
            selectedTasks.putAll(tasks);
        }
        handleView.refreshSelectRequire();
    }

    public void selectNotExist() {
        SaveRepository repository = SaveRepository.getInstance();
        tasks.forEach((id, task) -> {
            Task taskById = repository.getTaskById(id);
            if (taskById == null) selectedTasks.put(id, task);
        });
        handleView.refreshSelectRequire();
    }

    public void setRequiredTasks(HashSet<String> taskIds) {
        requiredTasks.clear();
        taskIds.forEach(id -> {
            Task task = tasks.get(id);
            if (task != null) requiredTasks.put(id, task);
        });
        refreshCheckBox();
        notifyDataSetChanged();
    }

    public void refreshCheckBox() {
        HashMap<String, Task> map = getAllSelectedTasks();
        if (tasks.isEmpty()) {
            handleView.setTaskCheck(-1);
        } else if (map.isEmpty()) {
            handleView.setTaskCheck(MaterialCheckBox.STATE_UNCHECKED);
        } else if (map.size() == tasks.size()) {
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
            String key = keys.get(index);
            Task task = tasks.get(key);
            if (select) {
                selectedTasks.put(key, task);
            } else {
                selectedTasks.remove(key);
            }
            handleView.refreshSelectRequire();
        }

        public void refreshItem(Task task) {
            binding.nameTitle.setText(task.getTitle());

            if (selectedTasks.containsKey(task.getId())) {
                binding.checkBox.setChecked(true);
                binding.checkBox.setEnabled(true);
            } else if (requiredTasks.containsKey(task.getId())) {
                binding.checkBox.setChecked(true);
                binding.checkBox.setEnabled(false);
            } else {
                binding.checkBox.setChecked(false);
                binding.checkBox.setEnabled(true);
            }
        }
    }
}
