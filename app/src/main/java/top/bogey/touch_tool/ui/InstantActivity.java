package top.bogey.touch_tool.ui;

import android.content.Intent;
import android.net.Uri;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.service.MainAccessibilityService;

public class InstantActivity extends BaseActivity {

    public static final String INTENT_KEY_SHOW_PLAY = "INTENT_KEY_SHOW_PLAY";
    public static final String INTENT_KEY_SHOW_TOAST = "INTENT_KEY_SHOW_TOAST";
    public static final String INTENT_KEY_DO_ACTION = "INTENT_KEY_DO_ACTION";

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

        String actionId = intent.getStringExtra(INTENT_KEY_DO_ACTION);
        if (actionId != null) {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                service.doOutAction(actionId, null);
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

        setIntent(null);
    }
}
