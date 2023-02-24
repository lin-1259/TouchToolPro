package top.bogey.touch_tool.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;

public class EmptyActivity extends BaseActivity {

    public static final String INTENT_KEY_SHOW_PLAY = "INTENT_KEY_SHOW_PLAY";
    public static final String INTENT_KEY_SHOW_TOAST = "INTENT_KEY_SHOW_TOAST";
    public static final String INTENT_KEY_QUICK_MENU = "INTENT_KEY_QUICK_MENU";
    public static final String INTENT_KEY_START_CAPTURE = "INTENT_KEY_START_CAPTURE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        String scheme = intent.getScheme();
        if (scheme != null) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            Uri uri = intent.getData();
            if (service != null && service.isServiceEnabled() && "do_action".equals(uri.getHost()) && uri.getQuery() != null) {
                service.doOutAction(uri.getQuery());
            }
        }

        int size = intent.getIntExtra(INTENT_KEY_SHOW_PLAY, -1);
        if (size >= 0) {
            handlePlayFloatView(size);
        }

        String msg = intent.getStringExtra(INTENT_KEY_SHOW_TOAST);
        if (msg != null) {
            showToast(msg);
        }

        boolean showQuickMenu = intent.getBooleanExtra(INTENT_KEY_QUICK_MENU, false);
        if (showQuickMenu) {
        }

        boolean startCaptureService = intent.getBooleanExtra(INTENT_KEY_START_CAPTURE, false);
        if (startCaptureService) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceConnected()) {
                Intent serviceIntent = new Intent(this, MainAccessibilityService.class);
                serviceIntent.putExtra(INTENT_KEY_START_CAPTURE, true);
                startService(serviceIntent);
            }
        }

        moveTaskToBack(true);
        finish();
    }
}
