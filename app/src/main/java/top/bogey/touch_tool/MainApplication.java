package top.bogey.touch_tool;

import android.app.Application;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;

public class MainApplication extends Application implements Thread.UncaughtExceptionHandler {
    private static MainActivity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static MainActivity getActivity() {
        return activity;
    }

    public static void setActivity(MainActivity activity) {
        MainApplication.activity = activity;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        e.printStackTrace();
        System.exit(996);
    }
}
