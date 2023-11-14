package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.databinding.FloatPickerPosBinding;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PosPickerFloatView extends BasePickerFloatView {
    private final FloatPickerPosBinding binding;

    private final Point point;
    private final Paint paint;
    private final int padding;
    int[] location = new int[2];
    private boolean isMarked;
    private boolean isInit = true;

    public PosPickerFloatView(@NonNull Context context, PickerCallback callback, PinPoint pinPoint) {
        super(context, callback);

        binding = FloatPickerPosBinding.inflate(LayoutInflater.from(context), this, true);
        point = new Point(pinPoint.getX(context), pinPoint.getY(context));

        isMarked = !(point.x == 0 && point.y == 0);

        binding.saveButton.setOnClickListener(v -> {
            pinPoint.setPoint(context, point.x, point.y);
            callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtils.getAttrColor(getContext(), R.attr.colorPrimaryLight, 0));
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        padding = Math.round(DisplayUtils.dp2px(context, 20));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLocationOnScreen(location);
        if (isInit && isMarked) refreshUI();
        isInit = false;
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isMarked) canvas.drawCircle(point.x - location[0], point.y - location[1], 5, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            isMarked = true;
            point.x = (int) x;
            point.y = (int) y;
        }
        refreshUI();
        return true;
    }

    private void refreshUI() {
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        if (isMarked) {
            binding.buttonBox.setX(point.x - binding.buttonBox.getWidth() / 2f - location[0]);
            if (point.y + padding * 2 + binding.buttonBox.getHeight() > binding.getRoot().getHeight()) {
                binding.buttonBox.setY(point.y - padding * 2 - binding.buttonBox.getHeight() - location[1]);
            } else {
                binding.buttonBox.setY(point.y + padding * 2 - location[1]);
            }
        }
        postInvalidate();
        if (binding.buttonBox.getWidth() == 0) {
            post(this::refreshUI);
        }
    }
}
