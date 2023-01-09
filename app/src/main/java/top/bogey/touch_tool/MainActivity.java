package top.bogey.touch_tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.PermissionResultCallback;
import top.bogey.touch_tool.utils.ResultCallback;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("touch_tool");
    }

    public static final String INTENT_KEY_BACKGROUND = "INTENT_KEY_BACKGROUND";
    public static final String INTENT_KEY_PLAY_PACKAGE = "INTENT_KEY_PLAY_PACKAGE";
    public static final String INTENT_KEY_QUICK_MENU = "INTENT_KEY_QUICK_MENU";
    public static final String INTENT_KEY_START_CAPTURE = "INTENT_KEY_START_CAPTURE";

    private ActivityMainBinding binding;

    private ActivityResultLauncher<Intent> intentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> contentLauncher;
    private PermissionResultCallback resultCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        MainApplication.setActivity(this);

        DisplayUtils.initParams(this);

        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (resultCallback != null) {
                resultCallback.onResult(result.getResultCode(), result.getData());
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result && resultCallback != null) resultCallback.onResult(RESULT_OK, null);
        });

        contentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result != null && resultCallback != null) {
                Intent intent = new Intent();
                intent.setData(result);
                resultCallback.onResult(RESULT_OK, intent);
            }
        });

        binding.getRoot().post(() -> {
            handleIntent(getIntent());
            setIntent(null);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.setActivity(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavController controller = Navigation.findNavController(this, R.id.conView);
        NavigationUI.setupWithNavController(binding.menuView, controller);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(R.id.home, R.id.task, R.id.setting).build();
        NavigationUI.setupActionBarWithNavController(this, controller, configuration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this, R.id.conView);
        return controller.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WorldState.getInstance().resetAppMap(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (intent == null) return;

        boolean isBackground = intent.getBooleanExtra(INTENT_KEY_BACKGROUND, false);
        if (isBackground) {
            moveTaskToBack(true);
        }

        String pkgName = intent.getStringExtra(INTENT_KEY_PLAY_PACKAGE);
        if (pkgName != null && !pkgName.isEmpty()) {
        }

        boolean showQuickMenu = intent.getBooleanExtra(INTENT_KEY_QUICK_MENU, false);
        if (showQuickMenu) {
        }

        boolean startCaptureService = intent.getBooleanExtra(INTENT_KEY_START_CAPTURE, false);
        if (startCaptureService) {
            Intent serviceIntent = new Intent(this, MainAccessibilityService.class);
            serviceIntent.putExtra(INTENT_KEY_START_CAPTURE, true);
            serviceIntent.putExtra(INTENT_KEY_BACKGROUND, isBackground);
            startService(serviceIntent);
        }
    }



    public void launchCapture(PermissionResultCallback callback) {
        resultCallback = callback;
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        intentLauncher.launch(manager.createScreenCaptureIntent());
    }

    public void launchNotification(PermissionResultCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String permission = Manifest.permission.POST_NOTIFICATIONS;
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                callback.onResult(Activity.RESULT_OK, null);
            } else if (shouldShowRequestPermissionRationale(permission)) {
                AppUtils.showDialog(this, R.string.capture_service_on_tips_4, result -> {
                    if (result) {
                        resultCallback = callback;
                        permissionLauncher.launch(permission);
                    } else {
                        callback.onResult(Activity.RESULT_CANCELED, null);
                    }
                });
            } else {
                resultCallback = callback;
                permissionLauncher.launch(permission);
            }
        } else {
            callback.onResult(Activity.RESULT_OK, null);
        }
    }
}