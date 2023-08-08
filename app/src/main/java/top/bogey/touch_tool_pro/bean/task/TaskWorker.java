package top.bogey.touch_tool_pro.bean.task;

import static top.bogey.touch_tool_pro.ui.InstantActivity.ACTION_ID;
import static top.bogey.touch_tool_pro.ui.InstantActivity.TASK_ID;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.TimeStartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class TaskWorker extends Worker {
    public TaskWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            Data inputData = getInputData();

            Task task = SaveRepository.getInstance().getTaskById(inputData.getString(TASK_ID));
            if (task == null) return Result.success();

            Action action = task.getActionById(inputData.getString(ACTION_ID));

            SaveRepository.getInstance().addLog(task.getId(), action.getFullDescription() + service.getString(R.string.work_execute, action.getTitle()));

            service.runTask(task, (TimeStartAction) action);
        }
        return Result.success();
    }
}
