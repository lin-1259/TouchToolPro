package top.bogey.touch_tool_pro.ui.custom;

import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class TouchPathFloatView extends FrameLayout implements FloatViewInterface {
    private final String tag;
    private final PinTouch touch;
    private final Paint paint;
    private final int lineWidth = 10;

    private final HashMap<Integer, Path> touchPath = new HashMap<>();
    private final ArrayList<PinTouch.TouchRecord> records;
    private int index;

    public TouchPathFloatView(@NonNull Context context, PinTouch touch) {
        super(context);
        this.touch = touch;
        records = touch.getRecords();
        tag = UUID.randomUUID().toString();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0));
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Rect area = touch.getRecordsArea();
        setMeasuredDimension(area.width() + 2 * lineWidth, area.height() + 2 * lineWidth);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        touchPath.forEach((id, path) -> canvas.drawPath(path, paint));
    }

    private void startAni() {
        if (records.size() > index) {
            PinTouch.TouchRecord record = records.get(index);
            record.getPoints().forEach(point -> {
                Path path = touchPath.get(point.getOwnerId());
                if (path == null) {
                    path = new Path();
                    touchPath.put(point.getOwnerId(), path);
                    path.moveTo(point.x + lineWidth, point.y + lineWidth);
                } else {
                    path.lineTo(point.x + lineWidth, point.y + lineWidth);
                }
            });
            index++;
            invalidate();
            postDelayed(this::startAni, record.getTime());
        } else {
            dismiss();
        }
    }

    @Override
    public void show() {
        Point point = touch.getAnchorPoint(getContext());

        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setGravity(FloatGravity.TOP_LEFT, point.x - lineWidth, point.y - lineWidth)
                .setDragEnable(false)
                .setAnimator(null)
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .show();

        long time = 0;
        for (GestureDescription.StrokeDescription stroke : touch.getStrokes(getContext(), 0)) {
            if (time < stroke.getDuration()) time = stroke.getDuration();
        }

        startAni();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(tag);
    }
}
