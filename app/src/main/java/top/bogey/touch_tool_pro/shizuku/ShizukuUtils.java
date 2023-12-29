package top.bogey.touch_tool_pro.shizuku;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicReference;

import rikka.shizuku.Shizuku;
import top.bogey.touch_tool_pro.BuildConfig;
import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.utils.ActivityResultCallback;
import top.bogey.touch_tool_pro.utils.AppUtils;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class ShizukuUtils {
    private final static int SHIZUKU_CODE = 16777114;
    private final static String SHIZUKU_SUFFIX = "UserService";
    private static IUserService userService = null;

    private final static Shizuku.UserServiceArgs ARGS =
            new Shizuku.UserServiceArgs(new ComponentName(BuildConfig.APPLICATION_ID, UserService.class.getName()))
                    .processNameSuffix(SHIZUKU_SUFFIX)
                    .debuggable(BuildConfig.DEBUG)
                    .version(BuildConfig.VERSION_CODE);

    private final static ServiceConnection USER_SERVICE_CONNECTION = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            userService = IUserService.Stub.asInterface(service);

            // 如果无障碍服务没开启，去开启
            if (!AppUtils.isAccessibilityServiceEnabled(MainApplication.getInstance())) {
                runCommand("pm grant top.bogey.touch_tool_pro.debug android.permission.WRITE_SECURE_SETTINGS");
                SettingSave.getInstance().setServiceEnabled(true);
                MainApplication.getInstance().getMainActivity().restartAccessibilityServiceBySecurePermission();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private static void initShizukuService() {
        if (userService == null) {
            Shizuku.bindUserService(ARGS, USER_SERVICE_CONNECTION);
        }
    }

    public static void peekShizukuService() {
        if (userService == null) {
            if (Shizuku.getVersion() < 12) {
                initShizukuService();
            } else {
                int version = Shizuku.peekUserService(ARGS, USER_SERVICE_CONNECTION);
                if (version == -1) {
                    initShizukuService();
                }
            }
        }
    }

    public static void destroyShizukuService() {
        if (userService != null) {
            Shizuku.unbindUserService(ARGS, USER_SERVICE_CONNECTION, true);
            userService = null;
        }
    }

    public static CmdResult runCommand(String cmd) {
        if (userService != null) {
            try {
                return userService.runCommand(cmd);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void requestShizukuPermission(ActivityResultCallback callback) {
        if (existShizuku()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                if (callback != null) callback.onResult(Activity.RESULT_OK, null);
            } else {
                AtomicReference<Shizuku.OnRequestPermissionResultListener> listener = new AtomicReference<>();
                listener.set((requestCode, grantResult) -> {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        if (callback != null) callback.onResult(Activity.RESULT_OK, null);
                    }
                    Shizuku.removeRequestPermissionResultListener(listener.get());
                    peekShizukuService();
                });
                Shizuku.addRequestPermissionResultListener(listener.get());
                Shizuku.requestPermission(SHIZUKU_CODE);
            }
        } else {
            Toast.makeText(MainApplication.getInstance(), R.string.no_shizuku, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean checkShizuku() {
        if (existShizuku()) {
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                peekShizukuService();
                return true;
            }
        }
        return false;
    }

    public static boolean existShizuku() {
        return Shizuku.pingBinder();
    }
}
