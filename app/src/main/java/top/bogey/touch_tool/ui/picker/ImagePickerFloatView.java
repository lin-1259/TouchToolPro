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
import android.widget.Toast;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.databinding.FloatPickerImageBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.FloatBaseCallback;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class ImagePickerFloatView extends BasePickerFloatView {
    private enum AdjustMode {NONE, MARK, BOTTOM_RIGHT, TOP_LEFT, DRAG, LEFT, RIGHT, TOP, BOTTOM}

    private final PinImage pinImage;

    private final FloatPickerImageBinding binding;
    private MainAccessibilityService service;

    private final Paint bitmapPaint;
    private Bitmap showBitmap;
    private final int[] location = new int[2];

    private final Paint markPaint;
    private Rect markArea = new Rect();
    private Rect matchArea = new Rect();

    private AdjustMode adjustMode = AdjustMode.NONE;
    private boolean isMarked = false;

    private int lastX = 0;
    private int lastY = 0;

    private final int offset;

    public ImagePickerFloatView(Context context, PickerCallback callback, PinImage pinImage) {
        super(context, callback);
        this.pinImage = pinImage;

        floatCallback = new ImagePickerCallback();

        binding = FloatPickerImageBinding.inflate(LayoutInflater.from(context), this, true);

        binding.saveButton.setOnClickListener(v -> {
            pinImage.setBitmap(context, getBitmap(), matchArea);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);
        bitmapPaint.setDither(true);

        offset = DisplayUtils.dp2px(context, 4);
    }

    public Bitmap getBitmap() {
        if (showBitmap != null) {
            Bitmap bitmap = Bitmap.createBitmap(showBitmap, markArea.left, markArea.top, markArea.width(), markArea.height());
            showBitmap.recycle();
            return bitmap;
        }
        return null;
    }

    public void realShow(int delay) {
        postDelayed(() -> {
            EasyFloat.show(tag);
            if (service != null && service.isCaptureEnabled() && service.binder != null) {
                Bitmap bitmap = service.binder.getCurrImage();
                if (bitmap != null) {
                    Point size = DisplayUtils.getScreenSize(service);
                    if (bitmap.getWidth() >= size.x && bitmap.getHeight() >= size.y) {
                        showBitmap = Bitmap.createBitmap(bitmap, location[0], location[1], getWidth(), getHeight());
                        Rect area = new Rect(pinImage.getArea(getContext()));
                        area.offset(-location[0], -location[1]);
                        Rect rect = service.binder.matchImage(showBitmap, pinImage.getScaleBitmap(getContext()), 95, area);
                        if (rect != null && area.contains(rect)) {
                            markArea = new Rect(rect);
                            isMarked = true;
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
        super.dispatchDraw(canvas);
        canvas.drawRect(markArea, markPaint);
        canvas.restore();

        drawChild(canvas, binding.areaBox, getDrawingTime());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        int localX = (int) event.getX();
        int localY = (int) event.getY();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            adjustMode = AdjustMode.NONE;
            lastX = x;
            lastY = y;

            View[] views = new View[]{binding.moveRight, binding.moveLeft, binding.markBox, binding.areaLeft, binding.areaRight, binding.areaTop, binding.areaBottom};
            int[] location = new int[2];
            for (int i = 0; i < views.length; i++) {
                View view = views[i];
                view.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
                if (rect.contains(x, y)) {
                    adjustMode = AdjustMode.values()[i + AdjustMode.BOTTOM_RIGHT.ordinal()];
                    // 对可能隐藏的控件做剔除
                    if (adjustMode.ordinal() <= AdjustMode.DRAG.ordinal() && !isMarked) {
                        adjustMode = AdjustMode.NONE;
                    }
                    break;
                }
            }

            // 没点到控制按钮
            if (adjustMode == AdjustMode.NONE) {
                // 判断是否点到区域内
                if (matchArea.contains(x, y)) {
                    isMarked = false;
                    adjustMode = AdjustMode.MARK;
                    markArea = new Rect(localX, localY, localX, localY);
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            int dx = x - lastX;
            int dy = y - lastY;
            switch (adjustMode) {
                case MARK:
                    if (matchArea.left <= x && matchArea.right >= x) {
                        markArea.right = localX;
                    }
                    if (matchArea.top <= y && matchArea.bottom >= y) {
                        markArea.bottom = localY;
                    }
                    break;
                case DRAG:
                    markArea.left = Math.max(matchArea.left - location[0], markArea.left + dx);
                    markArea.top = Math.max(matchArea.top - location[1], markArea.top + dy);
                    markArea.right = Math.min(matchArea.right - location[0], markArea.right + dx);
                    markArea.bottom = Math.min(matchArea.bottom - location[1], markArea.bottom + dy);
                    break;
                case TOP_LEFT:
                    markArea.left = Math.max(matchArea.left - location[0], markArea.left + dx);
                    markArea.top = Math.max(matchArea.top - location[1], markArea.top + dy);
                    break;
                case BOTTOM_RIGHT:
                    markArea.right = Math.min(matchArea.right - location[0], markArea.right + dx);
                    markArea.bottom = Math.min(matchArea.bottom - location[1], markArea.bottom + dy);
                    break;
                case LEFT:
                    matchArea.left = Math.max(location[0], matchArea.left + dx);
                    markArea.left = Math.max(matchArea.left - location[0], markArea.left);
                    break;
                case RIGHT:
                    matchArea.right = Math.min(location[0] + getWidth(), matchArea.right + dx);
                    markArea.right = Math.min(matchArea.right - location[0], markArea.right);
                    break;
                case TOP:
                    matchArea.top = Math.max(location[1], matchArea.top + dy);
                    markArea.top = Math.max(matchArea.top - location[1], markArea.top);
                    break;
                case BOTTOM:
                    matchArea.bottom = Math.min(location[1] + getHeight(), matchArea.bottom + dy);
                    markArea.bottom = Math.min(matchArea.bottom - location[1], markArea.bottom);
                    break;
            }
            markArea.sort();
            matchArea.sort();
            lastX = x;
            lastY = y;
        } else if (action == MotionEvent.ACTION_UP) {
            switch (adjustMode) {
                case MARK:
                    if (markArea.width() > 0 && markArea.height() > 0) {
                        isMarked = true;
                    }
                    break;
                case LEFT:
                case RIGHT:
                case TOP:
                case BOTTOM:
                    int px = DisplayUtils.dp2px(getContext(), 24 * 2);
                    if (matchArea.width() < px || matchArea.height() == px) {
                        initMatchArea();
                    }
                    break;
            }
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        binding.markBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        if (isMarked) {
            ViewGroup.LayoutParams params = binding.markBox.getLayoutParams();
            params.width = (int) markArea.width() + 2 * offset;
            params.height = (int) markArea.height() + 2 * offset;
            binding.markBox.setLayoutParams(params);

            binding.markBox.setX(markArea.left - offset);
            binding.markBox.setY(markArea.top - offset);

            binding.buttonBox.setX(markArea.left + (markArea.width() - binding.buttonBox.getWidth()) / 2f);
            if (markArea.bottom + offset * 2 + binding.buttonBox.getHeight() > getHeight()) {
                binding.buttonBox.setY(markArea.top - offset * 2 - binding.buttonBox.getHeight());
            } else {
                binding.buttonBox.setY(markArea.bottom + offset * 2);
            }
        }

        binding.areaBox.setX(matchArea.left - location[0]);
        binding.areaBox.setY(matchArea.top - location[1]);
        ViewGroup.LayoutParams params = binding.areaBox.getLayoutParams();
        params.width = matchArea.width();
        params.height = matchArea.height();
        binding.areaBox.setLayoutParams(params);

        postInvalidate();
    }

    private void initMatchArea() {
        matchArea = pinImage.getArea(getContext());
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
                ImagePickerFloatView.this.onShow();
                first = false;
            }
        }
    }
}
