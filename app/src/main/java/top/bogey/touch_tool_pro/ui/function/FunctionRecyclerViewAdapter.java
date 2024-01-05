package top.bogey.touch_tool_pro.ui.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.databinding.ViewFunctionListItemBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class FunctionRecyclerViewAdapter extends RecyclerView.Adapter<FunctionRecyclerViewAdapter.ViewHolder> {
    private final FunctionView functionView;
    private final ArrayList<Function> functions = new ArrayList<>();

    public FunctionRecyclerViewAdapter(FunctionView functionView) {
        this.functionView = functionView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewFunctionListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.refreshItem(functions.get(position));
    }

    @Override
    public int getItemCount() {
        return functions.size();
    }

    public void showFunctionsByTag(String tag) {
        functions.clear();
        if (functionView.task == null) {
            functions.addAll(SaveRepository.getInstance().getFunctionsByTag(tag));
        } else {
            functions.addAll(functionView.task.getFunctionsByTag(tag));
        }
        Collator collator = Collator.getInstance(Locale.CHINA);
        functions.sort((fun1, fun2) -> collator.compare(fun1.getTitle(), fun2.getTitle()));
        notifyDataSetChanged();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewFunctionListItemBinding binding;
        private final Context context;

        public ViewHolder(ViewFunctionListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            context = binding.getRoot().getContext();

            binding.getRoot().setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Function function = functions.get(index);
                if (functionView.isSelect) {
                    if (functionView.selectedFunctions.containsKey(function.getId())) {
                        functionView.selectedFunctions.remove(function.getId());
                    } else {
                        functionView.selectedFunctions.put(function.getId(), function);
                    }
                    notifyItemChanged(index);
                } else {
                    if (AppUtils.isRelease(context)) {
                        MainAccessibilityService service = MainApplication.getInstance().getService();
                        if (service == null || !service.isServiceConnected()) {
                            Toast.makeText(context, R.string.accessibility_service_off_tips, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    NavController controller = Navigation.findNavController(functionView.requireActivity(), R.id.conView);
                    String taskId = null;
                    if (functionView.task != null) taskId = functionView.task.getId();
                    controller.navigate(FunctionViewDirections.actionFunctionToBlueprint(taskId, function.getId()));
                }
            });

            binding.getRoot().setOnLongClickListener(v -> {
                int index = getBindingAdapterPosition();
                Function function = functions.get(index);
                if (functionView.isSelect) {
                    if (functionView.selectedFunctions.containsKey(function.getId())) {
                        functionView.selectedFunctions.remove(function.getId());
                    } else {
                        functionView.selectedFunctions.put(function.getId(), function);
                    }
                    notifyItemChanged(index);
                } else {
                    functionView.showBottomBar();
                    functionView.selectedFunctions.put(function.getId(), function);
                    notifyItemChanged(index);
                }
                return true;
            });

            binding.editButton.setOnClickListener(v -> {
                int index = getBindingAdapterPosition();
                Function function = functions.get(index);

                AppUtils.showEditDialog(context, R.string.task_change_title, function.getTitle(), result -> {
                    if (result != null && result.length() > 0) {
                        function.setTitle(result.toString());
                        binding.taskName.setText(result);
                        function.save();
                    }
                });
            });
        }

        public void refreshItem(Function function) {
            binding.taskName.setText(function.getTitle());

            String tagString = function.getTagString();
            binding.taskTag.setText(tagString);
            binding.taskTag.setVisibility(tagString.isEmpty() ? View.GONE : View.VISIBLE);

            ActionCheckResult result = function.check();
            if (result.type == ActionCheckResult.ActionResultType.ERROR) {
                binding.errorText.setVisibility(View.VISIBLE);
                binding.errorText.setText(result.tips);
            } else {
                binding.errorText.setVisibility(View.GONE);
            }

            binding.getRoot().setChecked(functionView.selectedFunctions.containsKey(function.getId()));
        }
    }
}