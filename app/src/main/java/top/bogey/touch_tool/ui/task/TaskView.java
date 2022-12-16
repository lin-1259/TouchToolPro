package top.bogey.touch_tool.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.start.CustomStartAction;
import top.bogey.touch_tool.data.action.start.ManualStartAction;
import top.bogey.touch_tool.data.action.start.NotificationStartAction;
import top.bogey.touch_tool.data.action.start.TimeStartAction;
import top.bogey.touch_tool.databinding.ViewTaskBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

public class TaskView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewTaskBinding binding = ViewTaskBinding.inflate(inflater, container, false);

        binding.getRoot().addView(new BaseCard<>(requireContext(), new Task(), new NotificationStartAction()));

        return binding.getRoot();
    }
}
