package top.bogey.touch_tool_pro.ui.function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashSet;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.save.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.databinding.ViewTagListBinding;
import top.bogey.touch_tool_pro.databinding.ViewTagListItemBinding;
import top.bogey.touch_tool_pro.utils.AppUtils;

public class FunctionTagView extends BottomSheetDialogFragment {
    private final FunctionView functionView;
    private final HashSet<String> commonTags = new HashSet<>();
    private ViewTagListBinding binding;

    public FunctionTagView(FunctionView functionView) {
        this.functionView = functionView;
        if (functionView.isSelect) {
            HashSet<String> tags = null;
            for (Function value : functionView.selectedFunctions.values()) {
                if (tags == null) {
                    tags = new HashSet<>();
                    if (value.getTags() != null) tags.addAll(value.getTags());
                } else {
                    if (value.getTags() != null) tags.removeIf(tag -> !value.getTags().contains(tag));
                }
            }
            if (tags != null) commonTags.addAll(tags);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewTagListBinding.inflate(inflater, container, false);

        binding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(requireContext(), R.string.tag_add, null, result -> {
            if (result != null && result.length() > 0) {
                ArrayList<String> tags = SaveRepository.getInstance().getFunctionTags();
                if (tags.contains(result.toString())) return;
                SaveRepository.getInstance().addFunctionTag(result.toString());
                binding.tagBox.removeAllViews();
                SaveRepository.getInstance().getFunctionTags().forEach(this::addTagChip);
            }
        }));

        SaveRepository.getInstance().getFunctionTags().forEach(this::addTagChip);

        return binding.getRoot();
    }

    private void addTagChip(String tag) {
        ViewTagListItemBinding itemBinding = ViewTagListItemBinding.inflate(LayoutInflater.from(getContext()), binding.tagBox, false);
        binding.tagBox.addView(itemBinding.getRoot());
        Chip chip = itemBinding.getRoot();
        chip.setText(tag);
        chip.setOnCloseIconClickListener(v -> AppUtils.showDialog(getContext(), R.string.tag_delete, result -> {
            if (result) {
                binding.tagBox.removeView(chip);
                SaveRepository.getInstance().removeFunctionTag(tag);
            }
        }));

        if (functionView.isSelect) {
            chip.setCheckable(true);
            chip.setChecked(commonTags.contains(tag));
            chip.setOnClickListener(v -> {
                if (commonTags.contains(tag)) {
                    commonTags.remove(tag);
                    functionView.selectedFunctions.values().forEach(function -> function.removeTag(tag));
                } else {
                    commonTags.add(tag);
                    functionView.selectedFunctions.values().forEach(function -> function.addTag(tag));
                }
            });
        } else {
            chip.setCheckable(false);
            chip.setChecked(false);
        }
    }

    @Override
    public void onDestroy() {
        if (functionView.task == null) {
            functionView.selectedFunctions.forEach((id, function) -> function.save());
        } else {
            functionView.task.save();
        }
        functionView.unSelectAll();
        functionView.hideBottomBar();
        functionView.reCalculateTags();
        super.onDestroy();
    }
}
