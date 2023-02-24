package top.bogey.touch_tool.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;

public class EmptyActivity extends Activity {

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
            MainAccessibilityService service = MainApplication.getService();
            Uri uri = intent.getData();
            if (service != null && service.isServiceEnabled() && "do_action".equals(uri.getHost()) && uri.getQuery() != null) {
                service.doOutAction(uri.getQuery());
            }
        }
        moveTaskToBack(true);
        finish();
    }
}
