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
import top.bogey.touch_tool_pro.ui.custom.ChangeAreaFloatView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class PosPickerFloatView extends BasePickerFloatView {
    private final FloatPickerPosBinding binding;

    private final Point point;
    private final Paint paint;
    private final int padding;
    int[] location = new int[2];
    private boolean isMarked;
    private boolean isInit = true;

    public PosPickerFloatView(@NonNull Context context, IPickerCallback callback, PinPoint pinPoint) {
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

        binding.detailButton.setOnClickListener(v -> new ChangeAreaFloatView(context, point, point -> refreshUI()).show());

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
        Point screenSize = DisplayUtils.getScreenSize(getContext());
        point.x = Math.min(screenSize.x, Math.max(location[0], point.x));
        point.y = Math.min(screenSize.y, Math.max(location[1], point.y));

        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        if (isMarked) {
            float x = point.x - binding.buttonBox.getWidth() / 2f - location[0];
            x = Math.max(0, Math.min(screenSize.x - binding.buttonBox.getWidth(), x));
            binding.buttonBox.setX(x);
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

    @Override
    public void dismiss() {
        EasyFloat.dismiss(ChangeAreaFloatView.class.getName());
        super.dismiss();
    }
}
