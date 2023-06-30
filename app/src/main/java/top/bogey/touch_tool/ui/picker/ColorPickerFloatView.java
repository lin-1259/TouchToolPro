package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.databinding.FloatPickerColorBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.FloatBaseCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class ColorPickerFloatView extends BasePickerFloatView {
    private final PinColor pinColor;

    private final FloatPickerColorBinding binding;
    private MainAccessibilityService service;

    private final Paint bitmapPaint;
    private Bitmap showBitmap;
    private final int[] location = new int[2];

    private final Paint markPaint;
    private List<Rect> markArea = new ArrayList<>();
    private int[] color;

    private boolean isMarked = false;

    public ColorPickerFloatView(Context context, PickerCallback callback, PinColor pinColor) {
        super(context, callback);
        this.pinColor = pinColor;

        floatCallback = new ImagePickerCallback();

        binding = FloatPickerColorBinding.inflate(LayoutInflater.from(context), this, true);

        binding.saveButton.setOnClickListener(v -> {
            pinColor.setColor(color);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.slider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (values.size() >= 2) {
                pinColor.setMinSize(values.get(0).intValue());
                pinColor.setMaxSize(values.get(values.size() - 1).intValue());
                pinColor.setScreen(DisplayUtils.getScreen(context));
            }
            refreshUI();
        });

        binding.slider.setLabelFormatter(value -> String.valueOf(Math.round(value)));

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);
    }

    public void realShow(int delay) {
        postDelayed(() -> {
            EasyFloat.show(tag);
            if (service != null && service.isCaptureEnabled()) {
                Bitmap bitmap = service.binder.getCurrImage();
                if (bitmap != null) {
                    Point size = DisplayUtils.getScreenSize(getContext());
                    if (bitmap.getWidth() >= size.x && bitmap.getHeight() >= size.y) {
                        showBitmap = DisplayUtils.safeCreateBitmap(bitmap, location[0], location[1], getWidth(), getHeight());
                        if (pinColor.isEmpty()) {
                            matchColor(pinColor.getColor(), pinColor.getMaxSize(getContext()), pinColor.getMinSize(getContext()));
                        }
                    }
                    refreshUI();
                    bitmap.recycle();
                }
            }
        }, delay);
    }

    public void onShow() {
        service = MainApplication.getInstance().getService();
        if (service != null && service.isServiceEnabled()) {
            if (!service.isCaptureEnabled()) {
                Toast.makeText(getContext(), R.string.capture_service_on_tips, Toast.LENGTH_SHORT).show();
                service.startCaptureService(true, result -> {
                    if (result) {
                        realShow(500);
                    } else {
                        dismiss();
                    }
                });
            } else {
                realShow(100);
            }
        } else {
            dismiss();
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        if (showBitmap != null && !showBitmap.isRecycled()) {
            canvas.drawBitmap(showBitmap, 0, 0, bitmapPaint);
        }

        canvas.saveLayer(getLeft(), getTop(), getRight(), getBottom(), bitmapPaint);
        long drawingTime = getDrawingTime();
        drawChild(canvas, binding.getRoot(), drawingTime);

        if (markArea != null) {
            for (int i = 0; i < markArea.size(); i++) {
                Rect rect = markArea.get(i);
                int size = rect.width() * rect.height();
                if (size >= pinColor.getMinSize(getContext()) && size <= pinColor.getMaxSize(getContext())) {
                    canvas.drawRect(rect, markPaint);
                }
            }
        }

        canvas.restore();

        if (isMarked) {
            drawChild(canvas, binding.buttonBox, drawingTime);
            drawChild(canvas, binding.slider, drawingTime);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int[] color = DisplayUtils.getHsvColor(showBitmap, (int) x, (int) y);
            matchColor(color);
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        binding.slider.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        postInvalidate();
    }

    private void matchColor(int[] color) {
        if (service == null || !service.isCaptureEnabled() || service.binder == null) return;
        this.color = color;
        if (color == null || color.length != 3) return;

        markArea = service.binder.matchColor(showBitmap, color, new Rect());
        if (markArea != null && markArea.size() > 0) {
            isMarked = true;

            binding.slider.setValueFrom(0);

            Rect max = markArea.get(0);
            int areaMaxSize = max.width() * max.height();
            binding.slider.setValueTo(areaMaxSize);

            Rect min = markArea.get(markArea.size() - 1);
            int areaMinSize = min.width() * min.height();
            binding.slider.setValueFrom(areaMinSize >= areaMaxSize ? areaMaxSize - 1 : areaMinSize);

            binding.slider.setValues((float) areaMinSize, (float) areaMaxSize);
        }
    }

    private void matchColor(int[] color, int maxSize, int minSize) {
        if (service == null || !service.isCaptureEnabled() || service.binder == null) return;
        this.color = color;
        if (color == null || color.length != 3) return;

        markArea = service.binder.matchColor(showBitmap, color, new Rect());
        if (markArea != null && markArea.size() > 0) {
            isMarked = true;

            binding.slider.setValueFrom(0);

            Rect max = markArea.get(0);
            int areaMaxSize = Math.max(max.width() * max.height(), maxSize);
            binding.slider.setValueTo(areaMaxSize);

            Rect min = markArea.get(markArea.size() - 1);
            int areaMinSize = Math.min(min.width() * min.height(), minSize);
            areaMinSize = areaMinSize >= areaMaxSize ? areaMaxSize - 1 : areaMinSize;
            binding.slider.setValueFrom(areaMinSize);
            minSize = Math.max(areaMinSize, minSize);

            binding.slider.setValues((float) minSize, (float) maxSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            getLocationOnScreen(location);
        }
    }

    protected class ImagePickerCallback extends FloatBaseCallback {
        private boolean first = true;

        @Override
        public void onShow(String tag) {
            if (first) {
                super.onShow("");
                ColorPickerFloatView.this.onShow();
                first = false;
            }
        }
    }
}
