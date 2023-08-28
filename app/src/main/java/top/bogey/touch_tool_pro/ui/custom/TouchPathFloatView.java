package top.bogey.touch_tool_pro.ui.custom;

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
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool_pro.MainApplication;
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
    private final HashSet<Point> touchPoints = new HashSet<>();
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
        paint.setStrokeWidth(lineWidth);
        touchPath.forEach((id, path) -> canvas.drawPath(path, paint));
        paint.setStrokeWidth(2 * lineWidth);
        touchPoints.forEach(point -> canvas.drawPoint(point.x, point.y, paint));
    }

    private void startAni() {
        if (records.size() > index) {
            touchPoints.clear();
            PinTouch.TouchRecord record = records.get(index);
            record.getPoints().forEach(point -> {
                Path path = touchPath.get(point.getOwnerId());
                if (path == null) {
                    path = new Path();
                    touchPath.put(point.getOwnerId(), path);
                    path.moveTo(point.x + lineWidth, point.y + lineWidth);
                }
                path.lineTo(point.x + lineWidth, point.y + lineWidth);
                touchPoints.add(new Point(point.x + lineWidth, point.y + lineWidth));
            });
            index++;
            invalidate();
            postDelayed(this::startAni, record.getTime());
        } else {
            animate().alpha(0).withEndAction(this::dismiss);
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

        startAni();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(tag);
    }
}
