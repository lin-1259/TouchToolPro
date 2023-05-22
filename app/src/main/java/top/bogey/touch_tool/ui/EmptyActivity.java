package top.bogey.touch_tool.ui;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;

public class EmptyActivity extends BaseActivity {

    public static final String INTENT_KEY_SHOW_PLAY = "INTENT_KEY_SHOW_PLAY";
    public static final String INTENT_KEY_SHOW_TOAST = "INTENT_KEY_SHOW_TOAST";
    public static final String INTENT_KEY_START_CAPTURE = "INTENT_KEY_START_CAPTURE";

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        handleIntent(intent);

        if (intent == null) {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        setIntent(null);
        moveTaskToBack(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        setIntent(null);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;

        String scheme = intent.getScheme();
        if (scheme != null) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            Uri uri = intent.getData();
            if (service != null && service.isServiceEnabled() && uri != null && "do_action".equals(uri.getHost()) && uri.getQuery() != null) {
                String actionId = null;
                HashMap<String, String> params = new HashMap<>();
                Set<String> names = uri.getQueryParameterNames();
                for (String name : names) {
                    UUID uuid = null;
                    try {
                        uuid = UUID.fromString(name);
                        actionId = uuid.toString();
                    } catch (IllegalArgumentException ignored) {
                    }
                    if (uuid == null) {
                        params.put(name, uri.getQueryParameter(name));
                    }
                }
                service.doOutAction(actionId, params);
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

        boolean startCaptureService = intent.getBooleanExtra(INTENT_KEY_START_CAPTURE, false);
        if (startCaptureService) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceConnected()) {
                service.startCaptureService(true, service.captureResultCallback);
            }
        }
    }
}
