package top.bogey.touch_tool;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.mmkv.MMKV;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

import top.bogey.touch_tool.ui.BaseActivity;
import top.bogey.touch_tool.utils.SettingSave;

public class MainApplication extends Application implements Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {
    private static MainApplication application;

    private WeakReference<BaseActivity> activity = new WeakReference<>(null);
    private WeakReference<MainAccessibilityService> service = new WeakReference<>(null);

    private Thread.UncaughtExceptionHandler handler;

    public static MainApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        MMKV.initialize(this);
        SettingSave.getInstance().addRunTimes();
        SettingSave.getInstance().setDynamicColor(this, SettingSave.getInstance().isDynamicColor());

        handler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        registerActivityLifecycleCallbacks(this);
    }

    public BaseActivity getActivity() {
        return activity.get();
    }

    public MainAccessibilityService getService() {
        return service.get();
    }

    public void setService(MainAccessibilityService service) {
        this.service = new WeakReference<>(service);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        String errorInfo = e.toString();
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            errorInfo = stringWriter.toString();
        } catch (Exception ignored) {
        }
        SettingSave.getInstance().setRunningError(errorInfo);
        handler.uncaughtException(t, e);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.activity = new WeakReference<>((BaseActivity) activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
