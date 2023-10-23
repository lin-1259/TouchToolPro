package top.bogey.touch_tool_pro.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.databinding.FloatManualChoiceExecuteItemBinding;

public class ManualChoiceRecyclerViewAdapter extends RecyclerView.Adapter<ManualChoiceRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<String> items;
    private final ManualChoiceFloatView parent;

    public ManualChoiceRecyclerViewAdapter(ManualChoiceFloatView parent, ArrayList<String> items) {
        this.parent = parent;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FloatManualChoiceExecuteItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.refreshItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    protected class ViewHolder extends RecyclerView.ViewHolder {
        public final FloatManualChoiceExecuteItemBinding binding;
        private final Context context;

        public ViewHolder(@NonNull FloatManualChoiceExecuteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                parent.selectItem(index);
            });
        }

        @SuppressLint("DefaultLocale")
        public void refreshItem(String item) {
            binding.titleText.setText(item);
        }
    }
}