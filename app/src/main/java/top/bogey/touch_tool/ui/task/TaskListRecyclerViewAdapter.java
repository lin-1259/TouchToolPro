package top.bogey.touch_tool.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.bogey.touch_tool.databinding.ViewTaskListBinding;

public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ViewHolder> {
    private final TaskView taskView;
    private final List<String> tags;

    public TaskListRecyclerViewAdapter(TaskView taskView, List<String> tags) {
        this.taskView = taskView;
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewTaskListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refreshView(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public String getTagByIndex(int index) {
        if (index >= 0 && index < tags.size()) {
            return tags.get(index);
        }
        return null;
    }

    public void addTag(String tag) {
        tags.add(tags.size() - 1, tag);
        notifyItemInserted(tags.indexOf(tag));
    }

    public void removeTag(String tag) {
        int index = tags.indexOf(tag);
        if (index >= 0) {
            tags.remove(tag);
            notifyItemRemoved(index);
        }
        notifyItemChanged(tags.size() - 1);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final TaskRecyclerViewAdapter adapter;

        public ViewHolder(@NonNull ViewTaskListBinding binding) {
            super(binding.getRoot());

            adapter = new TaskRecyclerViewAdapter(taskView);
            binding.getRoot().setAdapter(adapter);
        }

        public void refreshView(String tag) {
            adapter.showTasksByTag(tag);
        }
    }
}
