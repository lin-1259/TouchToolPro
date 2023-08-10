package top.bogey.touch_tool_pro.ui.function;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import top.bogey.touch_tool_pro.databinding.ViewFunctionListBinding;

public class FunctionListRecyclerViewAdapter extends RecyclerView.Adapter<FunctionListRecyclerViewAdapter.ViewHolder> {
    private final FunctionView functionView;
    private final ArrayList<String> tags = new ArrayList<>();

    public FunctionListRecyclerViewAdapter(FunctionView functionView) {
        this.functionView = functionView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewFunctionListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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
        Collator collator = Collator.getInstance(Locale.CHINA);
        this.tags.sort(collator::compare);
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final FunctionRecyclerViewAdapter adapter;

        public ViewHolder(@NonNull ViewFunctionListBinding binding) {
            super(binding.getRoot());

            adapter = new FunctionRecyclerViewAdapter(functionView);
            binding.getRoot().setAdapter(adapter);
        }

        public void refreshView(String tag) {
            adapter.showFunctionsByTag(tag);
        }
    }
}
