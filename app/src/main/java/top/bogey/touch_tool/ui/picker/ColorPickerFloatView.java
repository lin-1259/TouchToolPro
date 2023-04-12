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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private enum AdjustMode {NONE, MARK, LEFT, RIGHT, TOP, BOTTOM}

    private final PinColor pinColor;

    private final FloatPickerColorBinding binding;
    private MainAccessibilityService service;

    private final Paint bitmapPaint;
    private Bitmap showBitmap;
    private final int[] location = new int[2];

    private final Paint markPaint;
    private List<Rect> markArea = new ArrayList<>();
    private Rect matchArea = new Rect();
    private int[] color;

    private AdjustMode adjustMode = AdjustMode.NONE;
    private boolean isMarked = false;

    private float lastX = 0;
    private float lastY = 0;


    public ColorPickerFloatView(Context context, PickerCallback callback, PinColor pinColor) {
        super(context, callback);
        this.pinColor = pinColor;

        floatCallback = new ImagePickerCallback();

        binding = FloatPickerColorBinding.inflate(LayoutInflater.from(context), this, true);

        binding.saveButton.setOnClickListener(v -> {
            pinColor.setArea(matchArea);
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
                        showBitmap = Bitmap.createBitmap(bitmap, location[0], location[1], getWidth(), getHeight());
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
        if (service != null) {
            if (!service.isCaptureEnabled()) {
                Toast.makeText(getContext(), R.string.capture_service_on_tips, Toast.LENGTH_SHORT).show();
                service.startCaptureService(true, result -> {
                    if (result) {
                        realShow(500);
                    }
                });
            } else {
                realShow(100);
            }
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
        drawChild(canvas, binding.areaBox, drawingTime);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            adjustMode = AdjustMode.NONE;
            lastX = x;
            lastY = y;

            View[] views = new View[]{binding.areaLeft, binding.areaRight, binding.areaTop, binding.areaBottom};
            int[] location = new int[2];
            for (int i = 0; i < views.length; i++) {
                View view = views[i];
                view.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
                if (rect.contains((int) x, (int) y)) {
                    adjustMode = AdjustMode.values()[i + AdjustMode.LEFT.ordinal()];
                    break;
                }
            }

            // 没点到区域控制按钮
            if (adjustMode == AdjustMode.NONE) {
                // 判断是否点到区域内
                if (matchArea.contains((int) x, (int) y)) {
                    isMarked = false;
                    adjustMode = AdjustMode.MARK;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            float dx = x - lastX;
            float dy = y - lastY;
            switch (adjustMode) {
                case LEFT:
                    matchArea.left = Math.max(location[0], matchArea.left + (int) dx);
                    break;
                case RIGHT:
                    matchArea.right = Math.min(location[0] + getWidth(), matchArea.right + (int) dx);
                    break;
                case TOP:
                    matchArea.top = Math.max(location[1], matchArea.top + (int) dy);
                    break;
                case BOTTOM:
                    matchArea.bottom = Math.min(location[1] + getHeight(), matchArea.bottom + (int) dy);
                    break;
            }
            matchArea.sort();
            lastX = x;
            lastY = y;
        } else if (action == MotionEvent.ACTION_UP) {
            switch (adjustMode) {
                case NONE:
                    break;
                case LEFT:
                case RIGHT:
                case TOP:
                case BOTTOM:
                    int px = DisplayUtils.dp2px(getContext(), 24 * 2);
                    if (matchArea.width() < px || matchArea.height() == px) {
                        initMatchArea();
                    }
                    matchColor(color);
                    break;
                default:
                    int[] color = DisplayUtils.getHsvColor(showBitmap, (int) x, (int) y);
                    matchColor(color);
            }
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        binding.slider.setVisibility(isMarked ? VISIBLE : INVISIBLE);

        binding.areaBox.setX(matchArea.left - location[0]);
        binding.areaBox.setY(matchArea.top - location[1]);
        ViewGroup.LayoutParams params = binding.areaBox.getLayoutParams();
        params.width = matchArea.width();
        params.height = matchArea.height();
        binding.areaBox.setLayoutParams(params);

        ImageView[] images = new ImageView[] {binding.areaLeft, binding.areaTop, binding.areaRight, binding.areaBottom};
        int px = DisplayUtils.dp2px(getContext(), 24);
        Point size = DisplayUtils.getScreenSize(getContext());
        px = (int) (px * matchArea.width() * matchArea.height() * 1f / size.x / size.y);
        for (ImageView image : images) {
            ViewGroup.MarginLayoutParams layoutParams = (MarginLayoutParams) image.getLayoutParams();
            layoutParams.setMargins(px, px, px, px);
        }

        postInvalidate();
    }

    private void matchColor(int[] color) {
        if (!service.isCaptureEnabled() || service.binder == null) return;
        this.color = color;
        if (color == null || color.length != 3) return;

        markArea = service.binder.matchColor(showBitmap, color, matchArea);
        if (markArea != null && markArea.size() > 0) {
            isMarked = true;

            binding.slider.setValueFrom(0);

            Rect max = markArea.get(0);
            int areaMaxSize = max.width() * max.height();
            binding.slider.setValueTo(areaMaxSize);

            Rect min = markArea.get(markArea.size() - 1);
            int areaMinSize = min.width() * min.height();
            binding.slider.setValueFrom(areaMinSize == areaMaxSize ? areaMinSize - 1 : areaMinSize);

            binding.slider.setValues((float) areaMinSize, (float) areaMaxSize);
        }
    }

    private void matchColor(int[] color, int maxSize, int minSize) {
        if (!service.isCaptureEnabled() || service.binder == null) return;
        this.color = color;

        markArea = service.binder.matchColor(showBitmap, color, matchArea);
        if (markArea != null && markArea.size() > 0) {
            isMarked = true;

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

    private void initMatchArea() {
        matchArea = pinColor.getArea(getContext());
        matchArea.left = Math.max(matchArea.left, location[0]);
        matchArea.top = Math.max(matchArea.top, location[1]);
        matchArea.right = Math.min(matchArea.right, location[0] + getWidth());
        matchArea.bottom = Math.min(matchArea.bottom, location[1] + getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            getLocationOnScreen(location);
            initMatchArea();
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
