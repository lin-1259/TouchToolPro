package top.bogey.touch_tool;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.ui.custom.KeepAliveFloatView;
import top.bogey.touch_tool.ui.custom.ToastFloatView;
import top.bogey.touch_tool.ui.play.PlayFloatView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.PermissionResultCallback;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("touch_tool");
    }

    public static final String INTENT_KEY_BACKGROUND = "INTENT_KEY_BACKGROUND";
    public static final String INTENT_KEY_SHOW_PLAY = "INTENT_KEY_SHOW_PLAY";
    public static final String INTENT_KEY_SHOW_TOAST = "INTENT_KEY_SHOW_TOAST";
    public static final String INTENT_KEY_QUICK_MENU = "INTENT_KEY_QUICK_MENU";
    public static final String INTENT_KEY_START_CAPTURE = "INTENT_KEY_START_CAPTURE";

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

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolBar);

        MainApplication.setActivity(this);

        DisplayUtils.initParams(this);
        SettingSave.getInstance().addRunTimes();

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

        MainAccessibilityService.serviceEnabled.observe(this, aBoolean -> {
            if (MainApplication.getService() == null) return;
            if (aBoolean) {
                KeepAliveFloatView floatView = new KeepAliveFloatView(this);
                floatView.show();
            } else {
                EasyFloat.dismiss(KeepAliveFloatView.class.getCanonicalName());
            }
        });

        binding.getRoot().post(() -> {
            handleIntent(getIntent());
            setIntent(null);

            String runningError = SettingSave.getInstance().getRunningError();
            if (runningError != null && !runningError.isEmpty()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.report_running_error_tips)
                        .setPositiveButton(R.string.report_running_error_copy_and_join, (dialog, which) -> {
                            dialog.dismiss();
                            AppUtils.gotoUrl(this, "https://jq.qq.com/?_wv=1027&k=c1HOe3Gk");
                            copyError(runningError);
                        })
                        .setNegativeButton(R.string.report_running_error_copy, (dialog, which) -> {
                            dialog.dismiss();
                            copyError(runningError);
                        })
                        .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .show();

                SettingSave.getInstance().setRunningError(null);
            }
        });

        runFirstTimes();
    }

    private void copyError(String error) {
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.app_name), error);
        manager.setPrimaryClip(clipData);
        Toast.makeText(this, R.string.report_running_error_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.setActivity(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        setIntent(null);
    }

    private void runFirstTimes() {
        if (SettingSave.getInstance().getRunTimes() == 1) {
            try (InputStream inputStream = getAssets().open("default")) {
                byte[] bytes = new byte[inputStream.available()];
                if (inputStream.read(bytes) > 0) saveTasks(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleIntent(Intent intent) {
        if (intent == null) return;
        MainAccessibilityService service = MainApplication.getService();

        boolean isBackground = intent.getBooleanExtra(INTENT_KEY_BACKGROUND, false);
        if (isBackground) {
            moveTaskToBack(true);
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
            if (service != null && service.isServiceConnected()) {
                Intent serviceIntent = new Intent(this, MainAccessibilityService.class);
                serviceIntent.putExtra(INTENT_KEY_START_CAPTURE, true);
                serviceIntent.putExtra(INTENT_KEY_BACKGROUND, isBackground);
                startService(serviceIntent);
            }
        }

        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null) {
                saveTasksByFile(uri);
            }
        }
    }

    public void saveTasksByFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            byte[] bytes = new byte[inputStream.available()];
            int read = inputStream.read(bytes);
            if (read > 0)
                saveTasks(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTasks(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return;

        ArrayList<Task> tasks = new ArrayList<>();
        try {
            tasks = TaskRepository.getInstance().getGson().fromJson(new String(bytes), new TypeToken<ArrayList<Task>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tasks != null) {
            for (Task task : tasks) {
                TaskRepository.getInstance().saveTask(task);
            }
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
                AppUtils.showDialog(this, R.string.notification_on_tips, result -> {
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

    public void launcherContent(PermissionResultCallback callback) {
        resultCallback = callback;
        contentLauncher.launch("application/octet-stream");
    }

    public void handlePlayFloatView(int size) {
        runOnUiThread(() -> {
            PlayFloatView view = (PlayFloatView) EasyFloat.getView(PlayFloatView.class.getCanonicalName());
            if (size == 0) {
                if (view != null) view.setNeedRemove(true);
            } else {
                if (view == null) {
                    view = new PlayFloatView(this);
                    view.show();
                }
                view.onNewActions();
            }
        });
    }

    public void showToast(String msg) {
        runOnUiThread(() -> {
            ToastFloatView view = (ToastFloatView) EasyFloat.getView(ToastFloatView.class.getCanonicalName());
            if (view == null) {
                view = new ToastFloatView(this);
                view.show();
            }
            view.showToast(msg);
        });
    }
}