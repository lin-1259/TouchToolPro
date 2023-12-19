package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
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

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.databinding.FloatPickerAreaBinding;
import top.bogey.touch_tool_pro.ui.custom.ChangeAreaFloatView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class AreaPickerFloatView extends BasePickerFloatView {
    private final Rect area;
    private final FloatPickerAreaBinding binding;
    private final Paint markPaint;
    private final int[] location = new int[2];
    private AdjustMode adjustMode = AdjustMode.NONE;
    private int lastX = 0;
    private int lastY = 0;
    public AreaPickerFloatView(Context context, PickerCallback callback, PinArea pinArea) {
        super(context, callback);
        area = pinArea.getArea(context);

        binding = FloatPickerAreaBinding.inflate(LayoutInflater.from(context), this, true);

        binding.saveButton.setOnClickListener(v -> {
            pinArea.setArea(context, area);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.detailButton.setOnClickListener(v -> new ChangeAreaFloatView(context, area, area -> {
            area.left = Math.max(location[0], area.left);
            area.top = Math.max(location[1], area.top);
            area.right = Math.min(getWidth() + location[0], area.right);
            area.bottom = Math.min(getHeight() + location[1], area.bottom);
            refreshUI();
        }).show());

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        refreshUI();
    }

    @Override
    public void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        Rect rect = new Rect(area);
        rect.offset(-location[0], -location[1]);
        canvas.drawRect(rect, markPaint);
        drawChild(canvas, binding.areaBox, getDrawingTime());
        drawChild(canvas, binding.buttonBox, getDrawingTime());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

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
                if (rect.contains(x, y)) {
                    adjustMode = AdjustMode.values()[i + AdjustMode.LEFT.ordinal()];
                    break;
                }
            }

            // 没点到控制按钮
            if (adjustMode == AdjustMode.NONE) {
                // 判断是否点到区域内
                if (area.contains(x, y)) {
                    adjustMode = AdjustMode.DRAG;
                }
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            int dx = x - lastX;
            int dy = y - lastY;
            switch (adjustMode) {
                case DRAG -> {
                    area.left = Math.max(location[0], area.left + dx);
                    area.top = Math.max(location[1], area.top + dy);
                    area.right = Math.min(getWidth() + location[0], area.right + dx);
                    area.bottom = Math.min(getHeight() + location[1], area.bottom + dy);
                }
                case LEFT -> area.left = Math.max(location[0], area.left + dx);
                case RIGHT -> area.right = Math.min(getWidth() + location[0], area.right + dx);
                case TOP -> area.top = Math.max(location[1], area.top + dy);
                case BOTTOM -> area.bottom = Math.min(getHeight() + location[1], area.bottom + dy);
            }
            area.sort();
            lastX = x;
            lastY = y;
        } else if (action == MotionEvent.ACTION_UP) {
            if (adjustMode != AdjustMode.NONE) {
                int px = Math.round(DisplayUtils.dp2px(getContext(), 24 * 2));
                if (area.width() < px || area.height() < px) {
                    initMatchArea();
                }
            }
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        binding.areaBox.setX(area.left - location[0]);
        binding.areaBox.setY(area.top - location[1]);
        ViewGroup.LayoutParams params = binding.areaBox.getLayoutParams();
        params.width = area.width();
        params.height = area.height();
        binding.areaBox.setLayoutParams(params);

        ImageView[] images = new ImageView[]{binding.areaLeft, binding.areaTop, binding.areaRight, binding.areaBottom};
        int px = Math.round(DisplayUtils.dp2px(getContext(), 24));
        Point size = DisplayUtils.getScreenSize(getContext());
        px = (int) (px * area.width() * area.height() * 1f / size.x / size.y);
        for (ImageView image : images) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) image.getLayoutParams();
            layoutParams.setMargins(px, px, px, px);
        }

        postInvalidate();
    }

    private void initMatchArea() {
        area.left = Math.max(area.left, location[0]);
        area.top = Math.max(area.top, location[1]);
        area.right = Math.min(area.right, location[0] + getWidth());
        area.bottom = Math.min(area.bottom, location[1] + getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            getLocationOnScreen(location);
            initMatchArea();
            refreshUI();
        }
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ChangeAreaFloatView.class.getName());
        super.dismiss();
    }

    private enum AdjustMode {NONE, DRAG, LEFT, RIGHT, TOP, BOTTOM}
}
