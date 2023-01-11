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

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.databinding.FloatPickerColorBinding;
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

    private final Paint markPaint;
    public List<Rect> markArea = new ArrayList<>();
    private boolean isMarked = false;

    public ColorPickerFloatView(Context context, PickerCallback callback, PinColor pinColor) {
        super(context, callback);
        this.pinColor = pinColor;

        floatCallback = new ImagePickerCallback();

        binding = FloatPickerColorBinding.inflate(LayoutInflater.from(context), this, true);

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.slider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (values.size() >= 2) {
                pinColor.setMinSize(values.get(0).intValue());
                pinColor.setMaxSize(values.get(values.size() - 1).intValue());
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
                    int[] location = new int[2];
                    getLocationOnScreen(location);
                    Point size = DisplayUtils.getScreenSize(getContext());
                    showBitmap = Bitmap.createBitmap(bitmap, location[0], location[1], size.x - location[0], size.y - location[1]);
                    if (pinColor.isValid()) {
                        markArea = service.binder.matchColor(showBitmap, pinColor.getColor());
                        if (markArea != null && markArea.size() > 0) {
                            isMarked = true;
                            int minSize = pinColor.getMinSize(getContext());
                            int maxSize = pinColor.getMaxSize(getContext());
                            binding.slider.setValueFrom(0);
                            Rect max = markArea.get(0);
                            int areaMaxSize = Math.max(max.width() * max.height(), maxSize);
                            binding.slider.setValueTo(areaMaxSize);

                            Rect min = markArea.get(markArea.size() - 1);
                            int areaMinSize = Math.min(min.width() * min.height(), minSize);
                            binding.slider.setValueFrom(areaMinSize == areaMaxSize ? areaMinSize - 1 : areaMinSize);

                            binding.slider.setValues((float) minSize, (float) maxSize);
                        }
                    }
                    refreshUI();
                    bitmap.recycle();
                }
            }
        }, delay);
    }

    public void onShow() {
        service = MainApplication.getService();
        if (service != null) {
            if (!service.isCaptureEnabled()) {
                Toast.makeText(getContext(), R.string.capture_service_on_tips_2, Toast.LENGTH_SHORT).show();
                service.startCaptureService(true, result -> {
                    if (result) {
                        realShow(100);
                    }
                });
            } else {
                realShow(100);
            }
        } else {
            Toast.makeText(getContext(), R.string.capture_service_on_tips_3, Toast.LENGTH_SHORT).show();
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

        for (int i = 0; i < markArea.size(); i++) {
            Rect rect = markArea.get(i);
            int size = rect.width() * rect.height();
            if (size >= pinColor.getMinSize(getContext()) && size <= pinColor.getMaxSize(getContext())) {
                canvas.drawRect(rect, markPaint);
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
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMarked = false;
                break;
            case MotionEvent.ACTION_UP:
                if (service.isCaptureEnabled() && service.binder != null) {
                    int[] color = DisplayUtils.getHsvColor(showBitmap, (int) x, (int) y);
                    markArea = service.binder.matchColor(showBitmap, color);
                    if (markArea != null && markArea.size() > 0) {
                        pinColor.setColor(color);
                        pinColor.setScreen(DisplayUtils.getScreen(getContext()));
                        Rect max = markArea.get(0);
                        int maxSize = max.width() * max.height();
                        pinColor.setMaxSize(maxSize);
                        Rect min = markArea.get(markArea.size() - 1);
                        int minSize = min.width() * min.height();
                        pinColor.setMinSize(minSize);
                        isMarked = true;
                        binding.slider.setValueFrom(0);
                        binding.slider.setValueTo(maxSize);
                        binding.slider.setValueFrom(minSize == maxSize ? minSize - 1 : minSize);
                        binding.slider.setValues((float) pinColor.getMinSize(), (float) pinColor.getMaxSize());
                    } else {
                        dismiss();
                    }
                }
                break;
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        binding.slider.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        postInvalidate();
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
