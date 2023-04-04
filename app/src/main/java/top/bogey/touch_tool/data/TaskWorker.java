package top.bogey.touch_tool.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.start.TimeStartAction;

public class TaskWorker extends Worker {
    public final static String TASK = "taskId";
    public final static String ACTION = "actionId";

    public TaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            Data inputData = getInputData();
            Task task = TaskRepository.getInstance().getTaskById(inputData.getString(TASK));
            BaseAction action = task.getActionById(inputData.getString(ACTION));
            service.runTask(task.copy(), (TimeStartAction) action);
        }
        return Result.success();
    }
}
