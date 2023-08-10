package top.bogey.touch_tool_pro.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextItemBinding;

public class HandleFunctionAdapter extends RecyclerView.Adapter<HandleFunctionAdapter.ViewHolder> {
    private final HandleFunctionContextView handleView;
    private final ArrayList<Function> functions;
    private final ArrayList<Function> selectedFunctions = new ArrayList<>();

    public HandleFunctionAdapter(HandleFunctionContextView handleView, ArrayList<Function> functions) {
        this.handleView = handleView;
        this.functions = functions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogHandleActionContextItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.refreshItem(functions.get(position));
    }

    @Override
    public int getItemCount() {
        return functions.size();
    }

    public ArrayList<Function> getSelectedFunctions() {
        return selectedFunctions;
    }

    public void selectAll(boolean all) {
        selectedFunctions.clear();
        if (all) {
            selectedFunctions.addAll(functions);
        }
        refreshCheckBox();
        notifyDataSetChanged();
    }

    public void selectNotExist() {
        SaveRepository repository = SaveRepository.getInstance();
        functions.forEach(function -> {
            Function functionById = repository.getFunctionById(function.getId());
            if (functionById == null) selectedFunctions.add(function);
        });
        refreshCheckBox();
        notifyDataSetChanged();
    }

    public void refreshCheckBox() {
        if (functions.isEmpty()) {
            handleView.setFunctionCheck(-1);
        } else if (selectedFunctions.isEmpty()) {
            handleView.setFunctionCheck(MaterialCheckBox.STATE_UNCHECKED);
        } else if (selectedFunctions.size() == functions.size()) {
            handleView.setFunctionCheck(MaterialCheckBox.STATE_CHECKED);
        } else {
            handleView.setFunctionCheck(MaterialCheckBox.STATE_INDETERMINATE);
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
            Function function = functions.get(index);
            if (select) {
                if (!selectedFunctions.contains(function)) selectedFunctions.add(function);
            } else {
                selectedFunctions.remove(function);
            }
            refreshCheckBox();
        }

        public void refreshItem(Function function) {
            binding.nameTitle.setText(function.getTitle());
            binding.checkBox.setChecked(selectedFunctions.contains(function));
        }
    }
}
