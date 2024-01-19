package top.bogey.touch_tool_pro.ui.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import top.bogey.touch_tool_pro.databinding.ViewTaskListBinding;
import top.bogey.touch_tool_pro.save.SaveRepository;

public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.ViewHolder> {
    private final TaskView taskView;
    private final ArrayList<String> tags = new ArrayList<>();

    public TaskListRecyclerViewAdapter(TaskView taskView) {
        this.taskView = taskView;
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

    public void setTags(ArrayList<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        boolean shortcutRemove = this.tags.remove(SaveRepository.SHORTCUT_TAG);
        boolean noRemove = this.tags.remove(SaveRepository.NO_TAG);
        Collator collator = Collator.getInstance(Locale.CHINA);
        this.tags.sort(collator::compare);
        if (shortcutRemove) this.tags.add(SaveRepository.SHORTCUT_TAG);
        if (noRemove) this.tags.add(SaveRepository.NO_TAG);
        notifyDataSetChanged();
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
