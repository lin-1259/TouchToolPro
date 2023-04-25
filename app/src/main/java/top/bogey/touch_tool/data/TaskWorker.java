package top.bogey.touch_tool.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
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
            if (task == null) return Result.success();

            BaseAction action = task.getActionById(inputData.getString(ACTION));

            TaskRepository.getInstance().addLog(task, action.getTitle(service), service.getString(R.string.work_execute, action.getTitle(service)));

            service.runTask(task.copy(), (TimeStartAction) action);
        }
        return Result.success();
    }
}
