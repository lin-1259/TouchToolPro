package top.bogey.touch_tool_pro.ui;

import android.app.Activity;
import android.content.Intent;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;

public class PermissionActivity extends BaseActivity {
    public static final String INTENT_KEY_START_CAPTURE = "INTENT_KEY_START_CAPTURE";
    public static final String INTENT_KEY_MOVE_BACK = "INTENT_KEY_MOVE_BACK";

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;

        boolean startCaptureService = intent.getBooleanExtra(INTENT_KEY_START_CAPTURE, false);
        if (startCaptureService) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service == null || !service.isServiceConnected()) {
                finish();
                return;
            }

            launchNotification((notifyCode, notifyIntent) -> {
                if (notifyCode == Activity.RESULT_OK) {
                    launchCapture((code, data) -> {
                        service.bindCaptureService(code == Activity.RESULT_OK, data);
                        finish();
                    });
                } else {
                    service.callStartCaptureFailed();
                    finish();
                }
            });
            return;
        }
        finish();
    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        if (intent != null) {
            boolean moveBack = intent.getBooleanExtra(INTENT_KEY_MOVE_BACK, false);
            if (moveBack) moveTaskToBack(true);
        }
        super.finish();
    }
}
