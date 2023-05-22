package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.data.pin.object.PinTask;
import top.bogey.touch_tool.databinding.PinWidgetTaskPickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetTaskPicker extends BindingView<PinWidgetTaskPickerBinding> {
    private final ArrayAdapter<String> taskAdapter;
    private final ArrayAdapter<String> actionAdapter;
    private final PinTask pinTask;

    public PinWidgetTaskPicker(@NonNull Context context, PinTask pinTask) {
        this(context, null, pinTask);
    }

    public PinWidgetTaskPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinTask());
    }

    public PinWidgetTaskPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinTask pinTask) {
        super(context, attrs, PinWidgetTaskPickerBinding.class);
        if (pinTask == null) throw new RuntimeException("不是有效的引用");
        this.pinTask = pinTask;

        taskAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        binding.taskSpinner.setAdapter(taskAdapter);

        actionAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        binding.actionSpinner.setAdapter(actionAdapter);

        refreshTasks();
    }

    private void refreshTasks() {
        taskAdapter.clear();

        ArrayList<Task> allTasks = TaskRepository.getInstance().getAllTasks();
        if (allTasks.size() == 0) return;
        ArrayList<String> taskNames = new ArrayList<>();
        ArrayList<String> taskIds = new ArrayList<>();
        for (Task task : allTasks) {
            taskNames.add(task.getTitle());
            taskIds.add(task.getId());
        }
        int index = taskIds.indexOf(pinTask.getTaskId());
        if (index == -1) {
            index = 0;
            pinTask.setTaskId(taskIds.get(index));
        }

        taskAdapter.addAll(taskNames);
        binding.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pinTask.setTaskId(taskIds.get(position));
                refreshActions(allTasks.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.taskSpinner.setSelection(index);
    }

    private void refreshActions(Task task) {
        actionAdapter.clear();

        ArrayList<BaseAction> startActions = task.getActionsByClass(StartAction.class);
        if (startActions.size() == 0) return;

        ArrayList<String> actionNames = new ArrayList<>();
        ArrayList<String> actionIds = new ArrayList<>();
        for (BaseAction action : startActions) {
            actionNames.add(action.getTitle(getContext()));
            actionIds.add(action.getId());
        }
        int index = actionIds.indexOf(pinTask.getStartId());
        if (index == -1) {
            index = 0;
            pinTask.setStartId(actionIds.get(index));
        }

        actionAdapter.addAll(actionNames);
        binding.actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pinTask.setStartId(actionIds.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.actionSpinner.setSelection(index);
    }
}
