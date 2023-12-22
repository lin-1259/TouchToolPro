package top.bogey.touch_tool_pro.ui;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.OuterStartAction;
import top.bogey.touch_tool_pro.bean.action.start.StartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class InstantActivity extends BaseActivity {
    public static final String INTENT_KEY_DO_ACTION = "INTENT_KEY_DO_ACTION";

    public static final String TASK_ID = "TASK_ID";
    public static final String ACTION_ID = "ACTION_ID";

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        handleIntent(intent);
        moveTaskToBack(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        moveTaskToBack(true);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;

        Uri uri = intent.getData();
        if (uri != null) {
            if ("ttp".equals(uri.getScheme()) && "do_action".equals(uri.getHost()) && uri.getQuery() != null) {
                MainAccessibilityService service = MainApplication.getInstance().getService();
                if (service != null && service.isServiceEnabled()) {
                    HashMap<String, String> params = new HashMap<>();
                    for (String name : uri.getQueryParameterNames()) {
                        params.put(name, uri.getQueryParameter(name));
                    }
                    String taskId = params.remove(TASK_ID);
                    String actionId = params.remove(ACTION_ID);
                    if (taskId != null && actionId != null) {
                        Task task = SaveRepository.getInstance().getTaskById(taskId);
                        if (task != null) {
                            Action action = task.getActionById(actionId);
                            if (action instanceof OuterStartAction) {
                                FunctionContext context = (FunctionContext) task.copy();
                                params.forEach((key, value) -> {
                                    PinValue var = context.getVar(key);
                                    if (var != null) var.cast(value);
                                });

                                service.runTask(task, (OuterStartAction) action, context);
                            }
                        }
                    }
                }
            }
        }

        boolean doAction = intent.getBooleanExtra(INTENT_KEY_DO_ACTION, false);
        if (doAction) {
            String taskId = intent.getStringExtra(TASK_ID);
            String actionId = intent.getStringExtra(ACTION_ID);

            if (taskId != null && actionId != null) {
                Task task = SaveRepository.getInstance().getTaskById(taskId);
                if (task != null) {
                    Action action = task.getActionById(actionId);
                    MainAccessibilityService service = MainApplication.getInstance().getService();
                    if (service != null && service.isServiceEnabled()) {
                        if (action instanceof StartAction) {
                            service.runTask(task, (StartAction) action);
                        }
                    }
                }
            }
        }

        setIntent(null);
    }
}
