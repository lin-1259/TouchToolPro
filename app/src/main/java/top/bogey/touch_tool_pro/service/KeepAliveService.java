package top.bogey.touch_tool_pro.service;

import static top.bogey.touch_tool_pro.service.MainCaptureService.RUNNING_CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;

public class KeepAliveService extends Service {
    private final static int NOTIFICATION_ID = (int) (Math.random() * Integer.MAX_VALUE);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        MainApplication.getInstance().setKeepService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainApplication.getInstance().setKeepService(null);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel runningChannel = notificationManager.getNotificationChannel(RUNNING_CHANNEL_ID);
            if (runningChannel == null) {
                runningChannel = new NotificationChannel(RUNNING_CHANNEL_ID, getString(R.string.capture_service_running_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
                runningChannel.setDescription(getString(R.string.capture_service_running_channel_tips));
                notificationManager.createNotificationChannel(runningChannel);
            }

            Notification foregroundNotification = new NotificationCompat.Builder(this, RUNNING_CHANNEL_ID).build();
            startForeground(NOTIFICATION_ID, foregroundNotification);
        }
    }
}
