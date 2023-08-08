package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.databinding.PinWidgetTaskBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;

@SuppressLint("ViewConstructor")
public class PinWidgetTask extends PinWidget<PinTask> {
    private final PinWidgetTaskBinding binding;
    private ArrayAdapter<String> taskAdapter;
    private ArrayAdapter<String> actionAdapter;

    public PinWidgetTask(@NonNull Context context, ActionCard<?> card, PinView pinView, PinTask pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetTaskBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        taskAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        binding.taskSpinner.setAdapter(taskAdapter);

        actionAdapter = new ArrayAdapter<>(context, R.layout.pin_widget_spinner_item);
        binding.actionSpinner.setAdapter(actionAdapter);
        refreshTasks();
    }

    private void refreshTasks() {
        taskAdapter.clear();

        ArrayList<Task> allTasks = SaveRepository.getInstance().getAllTasks();
        if (allTasks.size() == 0) return;
        ArrayList<String> taskNames = new ArrayList<>();
        ArrayList<String> taskIds = new ArrayList<>();
        for (Task task : allTasks) {
            taskNames.add(task.getTitle());
            taskIds.add(task.getId());
        }
        int index = taskIds.indexOf(pinObject.getTaskId());
        if (index == -1) {
            index = 0;
            pinObject.setTaskId(taskIds.get(index));
        }

        taskAdapter.addAll(taskNames);
        binding.taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pinObject.setTaskId(taskIds.get(position));
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

        ArrayList<Action> startActions = task.getActionsByClass(StartAction.class);
        if (startActions.size() == 0) return;

        ArrayList<String> actionNames = new ArrayList<>();
        ArrayList<String> actionIds = new ArrayList<>();
        for (Action action : startActions) {
            actionNames.add(action.getTitle());
            actionIds.add(action.getId());
        }
        int index = actionIds.indexOf(pinObject.getStartId());
        if (index == -1) {
            index = 0;
            pinObject.setStartId(actionIds.get(index));
        }

        actionAdapter.addAll(actionNames);
        binding.actionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pinObject.setStartId(actionIds.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.actionSpinner.setSelection(index);
    }

    @Override
    public void initCustom() {

    }
}
