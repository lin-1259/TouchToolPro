package top.bogey.touch_tool_pro.ui.recorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.ui.picker.BasePickerFloatView;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class QuickRecordFloatView extends BasePickerFloatView {
    private final RecorderFloatView recorderFloatView;
    private final Paint paint;
    private final int[] location = new int[2];

    private ArrayList<PinTouch.TouchRecord> records;
    private long lastTime;
    private final Handler handler;

    public QuickRecordFloatView(Context context, RecorderFloatView recorderFloatView) {
        super(context, null);
        floatCallback = null;
        this.recorderFloatView = recorderFloatView;

        handler = new Handler();

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
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);

        if (records == null) return;
        for (ArrayList<Point> points : getPaths()) {
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
            case MotionEvent.ACTION_DOWN -> {
                records = new ArrayList<>();
                lastTime = System.currentTimeMillis();
                addTouchRecord(event, -1);
            }
            case MotionEvent.ACTION_MOVE, MotionEvent.ACTION_POINTER_DOWN -> addTouchRecord(event, -1);
            case MotionEvent.ACTION_POINTER_UP -> {
                int pointerId = event.getPointerId(event.getActionIndex());
                addTouchRecord(event, pointerId);
            }
            case MotionEvent.ACTION_UP -> {
                int pointerId = event.getPointerId(event.getActionIndex());
                addTouchRecord(event, pointerId);
                supportLongTouch(null);
                PinTouch pinTouch = new PinTouch(getContext(), records);
                recorderFloatView.addTouchStep(pinTouch);
            }
        }
        postInvalidate();
        return true;
    }

    private void addTouchRecord(MotionEvent event, int ownerId) {
        long currTime = System.currentTimeMillis();
        PinTouch.TouchRecord record = new PinTouch.TouchRecord((int) (currTime - lastTime));
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerId = event.getPointerId(i);
            float currX = event.getX(i) + location[0], currY = event.getY(i) + location[1];
            for (int j = 0; j < event.getHistorySize(); j++) {
                currX = event.getHistoricalX(i, j) + location[0];
                currY = event.getHistoricalY(i, j) + location[1];
            }
            PinTouch.PathPoint point = new PinTouch.PathPoint(pointerId, (int) currX, (int) currY);
            point.setEnd(ownerId == pointerId);
            record.addPoint(point);
        }
        addRecord(record);
        supportLongTouch(record.getPoints());
        lastTime = currTime;
    }

    private void supportLongTouch(HashSet<PinTouch.PathPoint> points) {
        handler.removeCallbacksAndMessages(null);
        if (points == null) return;
        handler.postDelayed(() -> {
            PinTouch.TouchRecord record = new PinTouch.TouchRecord(100);
            points.forEach(pathPoint -> record.addPoint(new PinTouch.PathPoint(pathPoint)));
            addRecord(record);
            lastTime = System.currentTimeMillis();
            supportLongTouch(points);
        }, 100);
    }

    private HashSet<ArrayList<Point>> getPaths() {
        HashSet<ArrayList<Point>> paths = new HashSet<>();
        HashMap<Integer, ArrayList<Point>> points = new HashMap<>();
        records.forEach(record -> record.getPoints().forEach(point -> {
            ArrayList<Point> list = points.computeIfAbsent(point.getOwnerId(), k -> new ArrayList<>());
            list.add(new Point(point));
            if (point.isEnd()) {
                paths.add(list);
                points.remove(point.getOwnerId());
            }
        }));
        paths.addAll(points.values());
        return paths;
    }


    private void addRecord(PinTouch.TouchRecord record) {
        if (!records.isEmpty()) {
            PinTouch.TouchRecord lastRecord = records.get(records.size() - 1);
            if (lastRecord.getPoints().size() < record.getPoints().size()) {
                records.clear();
            } else {
                lastRecord.getPoints().forEach(point -> {
                    // 新的手势少了点，把之前的点设置为结束点
                    if (record.getPointByOwnerId(point.getOwnerId()) == null) {
                        point.setEnd(true);
                    }
                });

                ArrayList<Integer> ownerIds = new ArrayList<>();
                record.getPoints().forEach(point -> {
                    // 上个点已经结束或者没了，这个点就得移除
                    PinTouch.PathPoint lastPoint = lastRecord.getPointByOwnerId(point.getOwnerId());
                    if (lastPoint == null || lastPoint.isEnd()) {
                        ownerIds.add(point.getOwnerId());
                    } else {
                        // 上个点还能接点，但是现在的点不能和上个点位置一致
                        if (lastPoint.equals(point.x, point.y)) {
                            point.offset(0, 1);
                        }
                    }
                });
                for (Integer ownerId : ownerIds) {
                    record.removePoint(ownerId);
                }
            }
        }
        if (record.isEmpty()) return;
        records.add(record);
    }
}