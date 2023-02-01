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
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.WorldState;
import top.bogey.touch_tool.databinding.ActivityMainBinding;
import top.bogey.touch_tool.ui.custom.ToastFloatView;
import top.bogey.touch_tool.ui.play.PlayFloatView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.PermissionResultCallback;
import top.bogey.touch_tool.utils.SettingSave;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;
import top.bogey.touch_tool.utils.easy_float.FloatGravity;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("touch_tool");
    }

    public static final String KEEP_ALIVE = "KEEP_ALIVE";

    public static final String INTENT_KEY_BACKGROUND = "INTENT_KEY_BACKGROUND";
    public static final String INTENT_KEY_SHOW_PLAY = "INTENT_KEY_SHOW_PLAY";
    public static final String INTENT_KEY_SHOW_TOAST = "INTENT_KEY_SHOW_TOAST";
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
                View view = LayoutInflater.from(this).inflate(R.layout.view_keep_alive, binding.getRoot(), false);
                EasyFloat.with(MainApplication.getService())
                        .setLayout(view)
                        .setTag(KEEP_ALIVE)
                        .setGravity(FloatGravity.TOP_CENTER, 0, DisplayUtils.dp2px(this, 2))
                        .setAlwaysShow(true)
                        .show();
            } else {
                EasyFloat.dismiss(KEEP_ALIVE);
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
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(R.id.home).build();
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
        setIntent(null);
    }

    public void handleIntent(Intent intent) {
        if (intent == null) return;

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
            Intent serviceIntent = new Intent(this, MainAccessibilityService.class);
            serviceIntent.putExtra(INTENT_KEY_START_CAPTURE, true);
            serviceIntent.putExtra(INTENT_KEY_BACKGROUND, isBackground);
            startService(serviceIntent);
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

        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        List<Task> tasks = parcel.createTypedArrayList(Task.CREATOR);

        if (tasks != null) {
            for (Task task : tasks) {
                TaskRepository.getInstance().saveTask(task);
            }
        }
        parcel.recycle();
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
        binding.getRoot().post(() -> {
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
        binding.getRoot().post(() -> {
            ToastFloatView view = (ToastFloatView) EasyFloat.getView(ToastFloatView.class.getCanonicalName());
            if (view == null) {
                view = new ToastFloatView(this);
                view.show();
            }
            view.showToast(msg);
        });
    }
}