package top.bogey.touch_tool.utils;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.state.ScreenStateAction;

public class AppUtils {
    public static native MatchResult nativeMatchTemplate(Bitmap bitmap, Bitmap temp, int method);

    public static native List<MatchResult> nativeMatchColor(Bitmap bitmap, int[] hsvColor);

    public static boolean isDebug(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        return applicationInfo != null && ((applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    public static void showDialog(Context context, int msg, ResultCallback callback) {
        showDialog(context, context.getString(msg), callback);
    }

    public static void showDialog(Context context, String msg, ResultCallback callback) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.dialog_title)
                .setMessage(msg)
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    dialog.dismiss();
                    if (callback != null) callback.onResult(true);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                    if (callback != null) callback.onResult(false);
                })
                .show();
    }

    public static void showEditDialog(Context context, @StringRes int title, CharSequence defaultValue, EditCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_text_input, null);
        TextInputEditText editText = view.findViewById(R.id.title_edit);
        editText.setText(defaultValue);

        new MaterialAlertDialogBuilder(context)
                .setPositiveButton(R.string.enter, (dialog, which) -> {
                    if (callback != null && editText.getText() != null)
                        callback.onResult(editText.getText().toString());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    if (callback != null) callback.onResult(defaultValue);
                    dialog.dismiss();
                })
                .setView(view)
                .setTitle(title)
                .show();
    }

    public static void gotoAppDetailSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void gotoApp(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(pkgName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
            }
        } catch (Exception ignored) {
        }
    }

    public static void gotoActivity(Context context, String pkgName, String activity) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName(pkgName, activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void gotoUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static void gotoBatterySetting(Context context) {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    public static boolean isIgnoredBattery(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
    }

    public static ScreenStateAction.ScreenState getScreenState(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = powerManager.isInteractive();
        if (screenOn) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            boolean locked = keyguardManager.isKeyguardLocked();
            if (locked) {
                return ScreenStateAction.ScreenState.SCREEN_LOCKED;
            } else {
                return ScreenStateAction.ScreenState.SCREEN_UNLOCKED;
            }
        }
        return ScreenStateAction.ScreenState.SCREEN_OFF;
    }

    public static void wakeScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, context.getString(R.string.common_package_name));
        wakeLock.acquire(100);
        wakeLock.release();
    }

    public static String formatDateLocalDate(Context context, long dateTime) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(dateTime);

        Calendar currCalendar = Calendar.getInstance();
        currCalendar.setTimeInMillis(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();
        if (timeCalendar.get(Calendar.YEAR) != currCalendar.get(Calendar.YEAR))
            builder.append(context.getString(R.string.year, timeCalendar.get(Calendar.YEAR)));
        builder.append(context.getString(R.string.month, timeCalendar.get(Calendar.MONTH) + 1));
        builder.append(context.getString(R.string.day, timeCalendar.get(Calendar.DAY_OF_MONTH)));
        return builder.toString();
    }

    public static String formatDateLocalTime(Context context, long dateTime) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(dateTime);

        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.hour, timeCalendar.get(Calendar.HOUR_OF_DAY)));
        if (timeCalendar.get(Calendar.MINUTE) != 0)
            builder.append(context.getString(R.string.minute, timeCalendar.get(Calendar.MINUTE)));
        return builder.toString();
    }

    public static String formatDateLocalMillisecond(Context context, long dateTime) {
        Calendar timeCalendar = Calendar.getInstance();
        timeCalendar.setTimeInMillis(dateTime);

        return context.getString(R.string.hour, timeCalendar.get(Calendar.HOUR_OF_DAY)) +
                context.getString(R.string.minute, timeCalendar.get(Calendar.MINUTE)) +
                context.getString(R.string.second, timeCalendar.get(Calendar.SECOND)) +
                context.getString(R.string.millisecond, timeCalendar.get(Calendar.MILLISECOND));
    }

    public static String formatDateLocalDuration(Context context, long dateTime) {
        int hours = (int) (dateTime / 1000 / 60 / 60);
        int minute = (int) (dateTime / 1000 / 60 % 60);

        StringBuilder builder = new StringBuilder();
        if (hours != 0) builder.append(context.getString(R.string.hours, hours));
        if (minute != 0) builder.append(context.getString(R.string.minutes, minute));
        return builder.toString();
    }

    public static long mergeDateTime(long date, long time) {
        Calendar baseCalendar = Calendar.getInstance();
        baseCalendar.setTimeInMillis(time);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(date);
        Calendar calendar = Calendar.getInstance();
        calendar.set(dateCalendar.get(Calendar.YEAR), dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DATE), baseCalendar.get(Calendar.HOUR_OF_DAY), baseCalendar.get(Calendar.MINUTE), 0);
        return calendar.getTimeInMillis();
    }
}
