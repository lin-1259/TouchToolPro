package top.bogey.touch_tool.ui.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.ui.picker.BasePickerFloatView;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.DouglasPeucker;

@SuppressLint("ViewConstructor")
public class QuickRecordFloatView extends BasePickerFloatView {
    private final RecorderFloatView recorderFloatView;
    private final Paint paint;
    private final int[] location = new int[2];

    private ArrayList<PinPath.TouchPath> paths = new ArrayList<>();
    private long touchStartTime = 0;

    public QuickRecordFloatView(Context context, RecorderFloatView recorderFloatView) {
        super(context, null);
        floatCallback = null;
        this.recorderFloatView = recorderFloatView;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtils.getAttrColor(getContext(), R.attr.colorPrimaryLight, 0));
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) getLocationOnScreen(location);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        for (PinPath.TouchPath touchPath : paths) {
            List<Point> points = touchPath.getPoints();
            if (points.size() >= 2) {
                Path path = new Path();
                for (Point point : points) {
                    if (path.isEmpty()) path.moveTo(point.x - location[0], point.y - location[1]);
                    else path.lineTo(point.x - location[0], point.y - location[1]);
                }
                canvas.drawPath(path, paint);
            }
            if (points.size() >= 1) {
                Point point = points.get(points.size() - 1);
                canvas.drawCircle(point.x - location[0], point.y - location[1], 5, paint);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchStartTime = System.currentTimeMillis();
                paths = new ArrayList<>();
                addNewPath(event);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int pointerId = event.getPointerId(i);
                    for (PinPath.TouchPath path : paths) {
                        if (path.getPointerId() == pointerId) {
                            float currX = 0, currY = 0;
                            for (int j = 0; j < event.getHistorySize(); j++) {
                                currX = event.getHistoricalX(i, j) + location[0];
                                currY = event.getHistoricalY(i, j) + location[1];
                            }
                            if (!(currX == 0 && currY == 0)) path.addPoint((int) currX, (int) currY);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                long touchTime = System.currentTimeMillis() - touchStartTime;
                paths.forEach(path -> path.setPoints(DouglasPeucker.compress(path.getPoints())));

                PinPath pinPath = new PinPath();
                pinPath.setPaths(getContext(), paths);
                recorderFloatView.addTouchStep(pinPath, (int) touchTime);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                int pointerId = event.getPointerId(event.getActionIndex());
                for (PinPath.TouchPath path : paths) {
                    if (path.getPointerId() == pointerId) {
                        path.setPointerId(-1);
                    }
                    break;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                addNewPath(event);
                break;
        }
        postInvalidate();
        return true;
    }

    private void addNewPath(MotionEvent event) {
        PinPath.TouchPath path = new PinPath.TouchPath();
        int pointerId = event.getPointerId(event.getActionIndex());
        path.setPointerId(pointerId);
        int x = (int) event.getX(event.findPointerIndex(pointerId));
        int y = (int) event.getY(event.findPointerIndex(pointerId));
        x = x + location[0];
        y = y + location[1];
        path.addPoint(x, y);
        paths.add(path);
    }
}