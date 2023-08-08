package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextItemBinding;

public class FunctionContextAdapter extends RecyclerView.Adapter<FunctionContextAdapter.ViewHolder> {
    private final ArrayList<FunctionContext> functionContexts = new ArrayList<>();
    private final ArrayList<FunctionContext> selectedContexts = new ArrayList<>();

    public FunctionContextAdapter(ArrayList<FunctionContext> functionContexts, ArrayList<FunctionContext> repeatActionContexts) {
        this.functionContexts.addAll(functionContexts);
        selectedContexts.addAll(functionContexts);
        if (repeatActionContexts != null) this.functionContexts.addAll(repeatActionContexts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogHandleActionContextItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refreshItem(functionContexts.get(position));
    }

    @Override
    public int getItemCount() {
        return functionContexts.size();
    }

    public ArrayList<FunctionContext> getSelectedContexts() {
        return selectedContexts;
    }

    public ArrayList<FunctionContext> getFunctionContexts() {
        return functionContexts;
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
            FunctionContext actionContext = functionContexts.get(index);
            if (select) {
                if (!selectedContexts.contains(actionContext)) selectedContexts.add(actionContext);
            } else {
                selectedContexts.remove(actionContext);
            }
        }

        public void refreshItem(FunctionContext actionContext) {
            binding.nameTitle.setText(actionContext.getTitle());
            binding.checkBox.setChecked(selectedContexts.contains(actionContext));
        }
    }
}
