package top.bogey.touch_tool_pro.ui.picker;

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

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.databinding.FloatPickerColorBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class ColorPickerFloatView extends BasePickerFloatView {
    private final PinColor pinColor;

    private final FloatPickerColorBinding binding;
    private final Paint bitmapPaint;
    private final int[] location = new int[2];
    private final Paint markPaint;
    private MainAccessibilityService service;
    private Bitmap showBitmap;
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
                pinColor.setArea(context, values.get(0).intValue(), values.get(values.size() - 1).intValue());
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
                service.getCurrImage(bitmap -> post(() -> {
                    if (bitmap != null) {
                        Point size = DisplayUtils.getScreenSize(getContext());
                        if (bitmap.getWidth() >= size.x && bitmap.getHeight() >= size.y) {
                            showBitmap = DisplayUtils.safeCreateBitmap(bitmap, location[0], location[1], getWidth(), getHeight());
                            matchColor(pinColor.getColor(), pinColor.getMin(getContext()), pinColor.getMax(getContext()));
                        }
                        refreshUI();
                    }
                }));
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
    public void dispatchDraw(@NonNull Canvas canvas) {
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
                if (size >= pinColor.getMin(getContext()) && size <= pinColor.getMax(getContext())) {
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
        binding.slider.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        postInvalidate();
    }

    private void matchColor(int[] color) {
        matchColor(color, 0, Integer.MAX_VALUE);
    }

    private void matchColor(int[] color, int currMin, int currMax) {
        if (service == null || !service.isCaptureEnabled()) return;
        this.color = color;
        if (color == null || color.length != 3) return;

        markArea = DisplayUtils.matchColor(showBitmap, color, new Rect(), 5);
        if (markArea != null && markArea.size() > 0) {
            isMarked = true;

            Rect max = markArea.get(0);
            int areaMaxSize = max.width() * max.height();

            Rect min = markArea.get(markArea.size() - 1);
            int areaMinSize = min.width() * min.height();

            refreshSlider(areaMinSize, areaMaxSize, currMin, currMax);
        }
    }

    private void refreshSlider(int minSize, int maxSize, int currMin, int currMax) {
        if (maxSize < minSize) {
            int tmp = maxSize;
            maxSize = minSize;
            minSize = tmp;
        }
        if (maxSize == minSize) maxSize++;

        currMin = Math.max(minSize, Math.min(currMin, maxSize));
        currMax = Math.max(currMin, Math.min(currMax, maxSize));
        if (currMin == currMax) {
            if (currMax == maxSize) currMin--;
            else currMax++;
        }

        binding.slider.setValueFrom(0);
        binding.slider.setValueTo(maxSize);
        binding.slider.setValueFrom(minSize);

        binding.slider.setValues((float) currMin, (float) currMax);
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
                first = false;
                ColorPickerFloatView.this.onShow();
            }
        }
    }
}
