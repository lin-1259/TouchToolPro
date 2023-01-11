package top.bogey.touch_tool.ui.task;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.convert.BoolConvertToAnd;
import top.bogey.touch_tool.data.action.convert.IntConvertToPosition;
import top.bogey.touch_tool.data.action.convert.PositionConvertToInt;
import top.bogey.touch_tool.data.action.logic.ForLoopLogicAction;
import top.bogey.touch_tool.data.action.operator.IntAddAction;
import top.bogey.touch_tool.data.action.operator.IntDivAction;
import top.bogey.touch_tool.data.action.state.ColorStateAction;
import top.bogey.touch_tool.data.action.state.ImageStateAction;
import top.bogey.touch_tool.data.action.state.WidgetStateAction;
import top.bogey.touch_tool.databinding.ViewTaskBlueprintBinding;

public class TaskView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewTaskBlueprintBinding binding = ViewTaskBlueprintBinding.inflate(inflater, container, false);

        Task task = new Task();
        ForLoopLogicAction startAction = new ForLoopLogicAction();
        task.addAction(startAction);
        startAction.x = 1;
        startAction.y = 1;

        ColorStateAction delayAction = new ColorStateAction();
        task.addAction(delayAction);
        delayAction.x = 1;
        delayAction.y = 5;

        ImageStateAction delayAction2 = new ImageStateAction();
        task.addAction(delayAction2);
        delayAction2.x = 1;
        delayAction2.y = 10;

        binding.cardLayout.setTask(task);

        return binding.getRoot();
    }
}
