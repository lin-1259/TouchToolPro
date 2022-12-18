package top.bogey.touch_tool.ui.task_blueprint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.action.DelayAction;
import top.bogey.touch_tool.data.action.start.AppStartAction;
import top.bogey.touch_tool.databinding.ViewTaskBlueprintBinding;

public class TaskBlueprintView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewTaskBlueprintBinding binding = ViewTaskBlueprintBinding.inflate(inflater, container, false);

        Task task = new Task();
        AppStartAction startAction = new AppStartAction();
        task.addAction(startAction);

        DelayAction delayAction = new DelayAction();
        task.addAction(delayAction);

        binding.cardLayout.setTask(task);

        return binding.getRoot();
    }
}
