package top.bogey.touch_tool;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.start.OutStartAction;
import top.bogey.touch_tool.data.action.start.StartAction;

public class ActionExportedService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String actionId = intent.getStringExtra("ActionId");
            if (actionId != null) {
                MainAccessibilityService service = MainApplication.getService();
                if (service != null && service.isServiceEnabled()) {
                    for (Task task : TaskRepository.getInstance().getAllTasks()) {
                        boolean flag = false;
                        for (StartAction startAction : task.getStartActions(OutStartAction.class)) {
                            if (startAction.isEnable() && startAction.getId().equals(actionId)) {
                                flag = true;
                                if (task.needCaptureService()) {
                                    service.showToast(service.getString(R.string.capture_service_on_tips));
                                    service.startCaptureService(true, result -> {
                                        if (result) service.runTask(task, startAction);
                                    });
                                } else {
                                    service.runTask(task, startAction);
                                }
                                break;
                            }
                        }
                        if (flag) break;
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
