package top.bogey.touch_tool;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

import top.bogey.touch_tool.service.KeepAliveService;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.ui.MainActivity;
import top.bogey.touch_tool.utils.SettingSave;

public class MainApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static MainApplication application;

    private WeakReference<MainActivity> activity = new WeakReference<>(null);
    private WeakReference<MainAccessibilityService> service = new WeakReference<>(null);
    private WeakReference<KeepAliveService> keepService = new WeakReference<>(null);

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
    }

    public MainActivity getActivity() {
        return activity.get();
    }

    public void setActivity(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public MainAccessibilityService getService() {
        return service.get();
    }

    public void setService(MainAccessibilityService service) {
        this.service = new WeakReference<>(service);
    }

    public KeepAliveService getKeepService() {
        return keepService.get();
    }

    public void setKeepService(KeepAliveService keepService) {
        this.keepService = new WeakReference<>(keepService);
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
}
