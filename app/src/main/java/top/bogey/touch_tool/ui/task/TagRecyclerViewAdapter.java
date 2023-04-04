package top.bogey.touch_tool.ui.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ViewTaskTabItemBinding;

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.ViewHolder> {

    private final TaskView taskView;
    private final List<String> tags;
    private final String NO;

    public TagRecyclerViewAdapter(TaskView taskView) {
        this.taskView = taskView;
        tags = TaskRepository.getInstance().getTags(taskView.requireContext());
        NO = this.taskView.getString(R.string.tag_no);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewTaskTabItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refreshView(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void addTag(String tag) {
        tags.add(tags.size() - 1, tag);
        notifyItemInserted(tags.indexOf(tag));
        taskView.addTag(tag);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewTaskTabItemBinding binding;
        private final Context context;

        public ViewHolder(ViewTaskTabItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                taskView.selectTag(index);
            });

            binding.deleteButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                String tag = tags.remove(index);
                TaskRepository.getInstance().removeTag(tag);
                taskView.removeTag(tag);
                notifyItemRemoved(index);
            });
        }

        public void refreshView(String tag) {
            binding.appName.setText(tag);
            binding.deleteButton.setVisibility(tag.equals(NO) ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
