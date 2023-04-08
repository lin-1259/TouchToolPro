package top.bogey.touch_tool.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.databinding.DialogHandleActionContextItemBinding;

public class ActionContextAdapter extends RecyclerView.Adapter<ActionContextAdapter.ViewHolder> {
    private final ArrayList<ActionContext> actionContexts;
    private final ArrayList<ActionContext> selectedContexts = new ArrayList<>();

    public ActionContextAdapter(ArrayList<ActionContext> actionContexts) {
        this.actionContexts = actionContexts;
        selectedContexts.addAll(actionContexts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogHandleActionContextItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refreshItem(actionContexts.get(position));
    }

    @Override
    public int getItemCount() {
        return actionContexts.size();
    }

    public ArrayList<ActionContext> getSelectedContexts() {
        return selectedContexts;
    }

    public ArrayList<ActionContext> getActionContexts() {
        return actionContexts;
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
            ActionContext actionContext = actionContexts.get(index);
            if (select) {
                if (!actionContexts.contains(actionContext)) selectedContexts.add(actionContext);
            } else {
                selectedContexts.remove(actionContext);
            }
        }

        public void refreshItem(ActionContext actionContext) {
            if (actionContext instanceof Task) {
                binding.nameTitle.setText(((Task) actionContext).getTitle());
            } else {
                binding.nameTitle.setText(((BaseFunction) actionContext).getTitle(context));
            }
            binding.checkBox.setChecked(selectedContexts.contains(actionContext));
        }
    }
}
