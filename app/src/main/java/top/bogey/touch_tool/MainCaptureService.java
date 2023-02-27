package top.bogey.touch_tool;

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
import android.graphics.Rect;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import top.bogey.touch_tool.utils.AppUtils;
import top.bogey.touch_tool.utils.MatchResult;

public class MainCaptureService extends Service {
    public static final String RUNNING_CHANNEL_ID = "RUNNING_CHANNEL_ID";
    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";

    public static final int NOTIFICATION_ID = 10000;

    private static final String STOP_CAPTURE = "StopCapture";
    public static final String DATA = "Data";

    private MediaProjection projection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private Timer timer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (projection == null) {
            MediaProjectionManager manager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            Intent data = intent.getParcelableExtra(DATA);
            projection = manager.getMediaProjection(Activity.RESULT_OK, data);
            projection.registerCallback(new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    stopService();
                }
            }, null);
            setVirtualDisplay();
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

        // 每分钟获取一张图，防止有些系统自动关闭录制
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (imageReader != null) {
                    Image image = imageReader.acquireNextImage();
                    if (image != null) image.close();
                }
            }
        }, 0, 60 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        if (virtualDisplay != null) virtualDisplay.release();
        if (imageReader != null) imageReader.close();
        if (projection != null) projection.stop();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        notificationManager.cancel(NOTIFICATION_ID + 1);
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

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.capture_service_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(getString(R.string.capture_service_notification_channel_tips));
            notificationManager.createNotificationChannel(channel);

            Notification foregroundNotification = new NotificationCompat.Builder(this, RUNNING_CHANNEL_ID).build();
            startForeground(NOTIFICATION_ID, foregroundNotification);

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
            notificationManager.notify(NOTIFICATION_ID + 1, closeNotification);
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

        public List<Rect> matchColor(Bitmap sourceBitmap, int[] color, Rect area) {
            if (sourceBitmap == null) return null;

            Bitmap bitmap = null;
            if (!(area.left == 0 && area.right == 0 && area.top == 0 && area.bottom == 0)) {
                if (sourceBitmap.getWidth() >= area.right && sourceBitmap.getHeight() >= area.bottom) {
                    sourceBitmap = Bitmap.createBitmap(sourceBitmap, area.left, area.top, area.width(), area.height());
                    bitmap = sourceBitmap;
                }
            }

            if (sourceBitmap == null) return null;
            List<MatchResult> matchResults = AppUtils.nativeMatchColor(sourceBitmap, color);
            if (bitmap != null) bitmap.recycle();

            if (matchResults != null) {
                matchResults.sort((o1, o2) -> o2.value - o1.value);
                List<Rect> rectList = new ArrayList<>();
                for (MatchResult matchResult : matchResults) {
                    matchResult.rect.offset(area.left, area.top);
                    rectList.add(matchResult.rect);
                }
                return rectList;
            }
            return null;
        }

        public List<Rect> matchColor(int[] color, Rect area) {
            Bitmap bitmap = getCurrImage();
            if (bitmap == null) return null;
            List<Rect> rectList = matchColor(bitmap, color, area);
            bitmap.recycle();
            return rectList;
        }

        public Rect matchImage(Bitmap sourceBitmap, Bitmap matchBitmap, int matchValue, Rect area) {
            if (sourceBitmap == null || matchBitmap == null) return null;

            Bitmap bitmap = null;
            if (!(area.left == 0 && area.right == 0 && area.top == 0 && area.bottom == 0)) {
                if (sourceBitmap.getWidth() >= area.right && sourceBitmap.getHeight() >= area.bottom) {
                    sourceBitmap = Bitmap.createBitmap(sourceBitmap, area.left, area.top, area.width(), area.height());
                    bitmap = sourceBitmap;
                }
            }

            if (sourceBitmap == null) return null;
            MatchResult matchResult = AppUtils.nativeMatchTemplate(sourceBitmap, matchBitmap, 5);
            if (bitmap != null) bitmap.recycle();

            if (Math.min(100, matchValue) > matchResult.value) return null;
            matchResult.rect.offset(area.left, area.top);
            return matchResult.rect;
        }

        public Rect matchImage(Bitmap matchBitmap, int matchValue, Rect area) {
            Bitmap bitmap = getCurrImage();
            if (bitmap == null) return null;
            Rect rect = matchImage(bitmap, matchBitmap, matchValue, area);
            bitmap.recycle();
            return rect;
        }

        public Bitmap getCurrImage() {
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
            } catch (Exception ignored) {
            }
            return null;
        }
    }
}
