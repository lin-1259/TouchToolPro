package top.bogey.touch_tool_pro.ui.custom;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.databinding.DialogCreateFunctionContextBinding;
import top.bogey.touch_tool_pro.databinding.ViewTagListItemBinding;
import top.bogey.touch_tool_pro.utils.ResultCallback;

public class CreateFunctionContextDialogBuilder extends MaterialAlertDialogBuilder {
    private final DialogCreateFunctionContextBinding binding;
    private final ArrayList<String> tags = new ArrayList<>();
    private ResultCallback callback;

    public CreateFunctionContextDialogBuilder(@NonNull Context context, ArrayList<String> tags, String current) {
        super(context);
        if (current != null) this.tags.add(current);
        binding = DialogCreateFunctionContextBinding.inflate(LayoutInflater.from(context), null, false);
        setView(binding.getRoot());

        for (String tag : tags) {
            ViewTagListItemBinding itemBinding = ViewTagListItemBinding.inflate(LayoutInflater.from(getContext()), binding.tagBox, false);
            binding.tagBox.addView(itemBinding.getRoot());
            Chip chip = itemBinding.getRoot();
            chip.setCloseIconVisible(false);

            chip.setText(tag);
            chip.setChecked(this.tags.contains(tag));
            chip.setOnClickListener(v -> {
                if (this.tags.contains(tag)) {
                    this.tags.remove(tag);
                } else {
                    this.tags.add(tag);
                }
            });
        }

        setPositiveButton(context.getString(R.string.enter), (dialog, which) -> {
            dialog.dismiss();
            if (callback != null) callback.onResult(true);
        });

        setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> {
            dialog.dismiss();
            if (callback != null) callback.onResult(false);
        });
    }

    public void setCallback(ResultCallback callback) {
        this.callback = callback;
    }

    public String getTitle() {
        Editable text = binding.titleEdit.getText();
        if (text != null && text.length() > 0) return text.toString();
        return "";
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}
