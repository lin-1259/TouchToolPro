package top.bogey.touch_tool_pro.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.nio.ByteBuffer;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;

public class MainCaptureService extends Service {
    public static final String RUNNING_CHANNEL_ID = "RUNNING_CHANNEL_ID";
    public static final String DATA = "Data";
    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    private static final int CANCEL_NOTIFICATION_ID = 10000;
    private static final String STOP_CAPTURE = "StopCapture";
    private MediaProjection projection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (projection == null) {
            MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent data = intent.getParcelableExtra(DATA);
            if (data != null) {
                projection = manager.getMediaProjection(Activity.RESULT_OK, data);
                projection.registerCallback(new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        stopService();
                    }
                }, null);
                setVirtualDisplay();
            }
        }
        return new CaptureServiceBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (virtualDisplay != null) virtualDisplay.release();
        if (imageReader != null) imageReader.close();
        if (projection != null) projection.stop();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(CANCEL_NOTIFICATION_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra(STOP_CAPTURE, false)) {
                stopService();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (virtualDisplay != null) virtualDisplay.release();
        if (imageReader != null) imageReader.close();

        if (projection != null) setVirtualDisplay();
        else stopService();
    }

    private void stopService() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service != null) {
            service.stopCaptureService();
        } else {
            stopSelf();
        }
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
            startForeground((int) (Math.random() * Integer.MAX_VALUE), foregroundNotification);

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.capture_service_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.capture_service_notification_channel_tips));
            notificationManager.createNotificationChannel(channel);

            Intent intent = new Intent(this, MainCaptureService.class);
            intent.putExtra(STOP_CAPTURE, true);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            Notification closeNotification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.capture_service_notification_title))
                    .setContentText(getString(R.string.capture_service_notification_text))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();

            closeNotification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(CANCEL_NOTIFICATION_ID, closeNotification);
        }
    }

    @SuppressLint("WrongConstant")
    private void setVirtualDisplay() {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getRealMetrics(metrics);
        imageReader = ImageReader.newInstance(metrics.widthPixels, metrics.heightPixels, PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(reader -> Log.d("TAG", "OnImageAvailable"), null);
        virtualDisplay = projection.createVirtualDisplay("CaptureService", metrics.widthPixels, metrics.heightPixels, metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
    }

    public class CaptureServiceBinder extends Binder {

        public synchronized Bitmap getCurrImage() {
            try (Image image = imageReader.acquireLatestImage()) {
                if (image == null) return null;
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int width = image.getWidth();
                int height = image.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width + (rowStride - pixelStride * width) / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                return bitmap;
            } catch (Exception | Error ignored) {
            }
            return null;
        }
    }
}
