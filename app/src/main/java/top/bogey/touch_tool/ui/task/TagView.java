package top.bogey.touch_tool.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.databinding.ViewTaskTabBinding;
import top.bogey.touch_tool.utils.AppUtils;

public class TagView extends BottomSheetDialogFragment {
    private final TaskView taskView;

    public TagView(TaskView taskView) {
        this.taskView = taskView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewTaskTabBinding binding = ViewTaskTabBinding.inflate(inflater, container, false);

        TagRecyclerViewAdapter adapter = new TagRecyclerViewAdapter(taskView);
        binding.tagBox.setAdapter(adapter);

        binding.addButton.setOnClickListener(v -> AppUtils.showEditDialog(requireContext(), R.string.tag_add, null, result -> {
            if (result != null && result.length() > 0) {
                TaskRepository.getInstance().addTag(result.toString());
                adapter.addTag(result.toString());
            }
        }));

        return binding.getRoot();
    }
}
