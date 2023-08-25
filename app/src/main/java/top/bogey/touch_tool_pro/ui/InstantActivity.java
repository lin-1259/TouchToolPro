package top.bogey.touch_tool_pro.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.HashMap;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.start.OuterStartAction;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.task.Task;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class InstantActivity extends BaseActivity {
    public static final String INTENT_KEY_SHOW_PLAY = "INTENT_KEY_SHOW_PLAY";
    public static final String INTENT_KEY_SHOW_TOAST = "INTENT_KEY_SHOW_TOAST";
    public static final String INTENT_KEY_SHOW_TOUCH = "INTENT_KEY_SHOW_TOUCH";

    public static final String INTENT_KEY_DO_ACTION = "INTENT_KEY_DO_ACTION";
    public static final String TASK_ID = "TASK_ID";
    public static final String ACTION_ID = "ACTION_ID";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().setInstantActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().setInstantActivity(null);
    }

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
                    if (action instanceof OuterStartAction) {
                        MainAccessibilityService service = MainApplication.getInstance().getService();
                        if (service != null && service.isServiceEnabled()) {
                            service.runTask(task, (OuterStartAction) action);
                        }
                    }
                }
            }
        }

        int size = intent.getIntExtra(INTENT_KEY_SHOW_PLAY, -1);
        if (size >= 0) {
            handlePlayFloatView(size);
        }

        String touchJson = intent.getStringExtra(INTENT_KEY_SHOW_TOUCH);
        if (touchJson != null && !touchJson.isEmpty()) {
            PinTouch touch = (PinTouch) GsonUtils.getAsObject(touchJson, PinObject.class, null);
            showTouch(touch);
        }

        String msg = intent.getStringExtra(INTENT_KEY_SHOW_TOAST);
        if (msg != null) {
            showToast(msg);
        }

        setIntent(null);
    }
}
