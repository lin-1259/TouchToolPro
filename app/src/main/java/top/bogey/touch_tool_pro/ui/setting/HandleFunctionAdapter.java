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

import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.databinding.DialogHandleActionContextItemBinding;
import top.bogey.touch_tool_pro.save.SaveRepository;

public class HandleFunctionAdapter extends RecyclerView.Adapter<HandleFunctionAdapter.ViewHolder> {
    private final HandleFunctionContextView handleView;
    private final HashMap<String, Function> functions;
    private final ArrayList<String> keys;
    private final HashMap<String, Function> selectedFunctions = new HashMap<>();
    private final HashMap<String, Function> requireFunctions = new HashMap<>();

    public HandleFunctionAdapter(HandleFunctionContextView handleView, HashMap<String, Function> functions) {
        this.handleView = handleView;
        this.functions = functions;
        keys = new ArrayList<>(functions.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DialogHandleActionContextItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String key = keys.get(position);
        holder.refreshItem(Objects.requireNonNull(functions.get(key)));
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public HashMap<String, Function> getAllSelectedFunctions() {
        HashMap<String, Function> hashMap = new HashMap<>();
        hashMap.putAll(requireFunctions);
        hashMap.putAll(selectedFunctions);
        return hashMap;
    }

    public HashMap<String, Function> getSelectedFunctions() {
        return selectedFunctions;
    }

    public void selectAll(boolean all) {
        selectedFunctions.clear();
        if (all) {
            selectedFunctions.putAll(functions);
        }
        handleView.refreshSelectRequire();
    }

    public void selectNotExist() {
        SaveRepository repository = SaveRepository.getInstance();
        functions.forEach((id, function) -> {
            Function functionById = repository.getFunctionById(id);
            if (functionById == null) selectedFunctions.put(id, function);
        });
        handleView.refreshSelectRequire();
    }

    public void setRequireFunctions(HashSet<String> functionIds) {
        requireFunctions.clear();
        functionIds.forEach(id -> {
            Function function = functions.get(id);
            if (function != null) requireFunctions.put(id, function);
        });
        refreshCheckBox();
        notifyDataSetChanged();
    }

    public void refreshCheckBox() {
        HashMap<String, Function> map = getAllSelectedFunctions();
        if (functions.isEmpty()) {
            handleView.setFunctionCheck(-1);
        } else if (map.isEmpty()) {
            handleView.setFunctionCheck(MaterialCheckBox.STATE_UNCHECKED);
        } else if (map.size() == functions.size()) {
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
            String key = keys.get(index);
            Function function = functions.get(key);
            if (select) {
                selectedFunctions.put(key, function);
            } else {
                selectedFunctions.remove(key);
            }
            requireFunctions.clear();
            handleView.refreshSelectRequire();
        }

        public void refreshItem(Function function) {
            binding.nameTitle.setText(function.getTitle());

            if (selectedFunctions.containsKey(function.getId())) {
                binding.checkBox.setChecked(true);
                binding.checkBox.setEnabled(true);
            } else if (requireFunctions.containsKey(function.getId())) {
                binding.checkBox.setChecked(true);
                binding.checkBox.setEnabled(false);
            } else {
                binding.checkBox.setChecked(false);
                binding.checkBox.setEnabled(true);
            }
        }
    }
}
