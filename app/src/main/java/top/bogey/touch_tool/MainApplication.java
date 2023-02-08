package top.bogey.touch_tool;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.tencent.mmkv.MMKV;

import java.io.PrintWriter;
import java.io.StringWriter;

import top.bogey.touch_tool.utils.SettingSave;

public class MainApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static MainActivity activity;
    private static MainAccessibilityService service;

    private Thread.UncaughtExceptionHandler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        DynamicColors.applyToActivitiesIfAvailable(this);

        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static MainActivity getActivity() {
        return activity;
    }

    public static void setActivity(MainActivity activity) {
        MainApplication.activity = activity;
    }

    public static MainAccessibilityService getService() {
        return service;
    }

    public static void setService(MainAccessibilityService service) {
        MainApplication.service = service;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        String errorInfo = e.toString();
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            errorInfo = stringWriter.toString();
        } catch (Exception ignored) {}
        SettingSave.getInstance().setRunningError(errorInfo);
        handler.uncaughtException(t, e);
    }
}
