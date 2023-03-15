package top.bogey.touch_tool.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.ui.custom.ToastFloatView;
import top.bogey.touch_tool.ui.play.PlayFloatView;
import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.PermissionResultCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

public class BaseActivity extends AppCompatActivity {
    static {
        System.loadLibrary("touch_tool");
    }

    private ActivityResultLauncher<Intent> intentLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> contentLauncher;
    private PermissionResultCallback resultCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(params);
        }

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
    }

    public void launchCapture(PermissionResultCallback callback) {
        if (intentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        resultCallback = callback;
        MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        intentLauncher.launch(manager.createScreenCaptureIntent());
    }

    public void launchNotification(PermissionResultCallback callback) {
        if (permissionLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String permission = Manifest.permission.POST_NOTIFICATIONS;
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                if (callback != null) callback.onResult(Activity.RESULT_OK, null);
            } else if (shouldShowRequestPermissionRationale(permission)) {
                AppUtils.showDialog(this, R.string.notification_on_tips, result -> {
                    if (result) {
                        resultCallback = callback;
                        permissionLauncher.launch(permission);
                    } else {
                        if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
                    }
                });
            } else {
                resultCallback = callback;
                permissionLauncher.launch(permission);
            }
        } else {
            if (callback != null) callback.onResult(Activity.RESULT_OK, null);
        }
    }

    public void launcherContent(PermissionResultCallback callback) {
        if (contentLauncher == null) {
            if (callback != null) callback.onResult(Activity.RESULT_CANCELED, null);
            return;
        }
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
