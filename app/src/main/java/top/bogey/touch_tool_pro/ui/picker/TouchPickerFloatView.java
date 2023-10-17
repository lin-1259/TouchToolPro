package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.databinding.FloatPickerTouchBinding;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class TouchPickerFloatView extends BasePickerFloatView {
    private final FloatPickerTouchBinding binding;

    private final ArrayList<PinTouch.TouchRecord> records;
    private PinTouch.TouchAnchor anchor;
    private long lastTime;
    private final Handler handler;

    private float lastX = 0;
    private float lastY = 0;

    int[] location = new int[2];

    private final Paint paint;
    private final Rect realArea = new Rect();
    private boolean isMarked;

    private final int padding;
    private boolean isInit = true;

    private boolean isClick = false;

    public TouchPickerFloatView(@NonNull Context context, PickerCallback callback, PinTouch pinTouch) {
        super(context, callback);

        binding = FloatPickerTouchBinding.inflate(LayoutInflater.from(context), this, true);

        records = pinTouch.getRecords(context);
        isMarked = !records.isEmpty();

        binding.saveButton.setOnClickListener(v -> {
            pinTouch.setRecords(context, records, anchor);
            callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.topLeftButton.setOnClickListener(v -> refreshGravityButton(PinTouch.TouchAnchor.TOP_LEFT));
        binding.topRightButton.setOnClickListener(v -> refreshGravityButton(PinTouch.TouchAnchor.TOP_RIGHT));
        binding.bottomLeftButton.setOnClickListener(v -> refreshGravityButton(PinTouch.TouchAnchor.BOTTOM_LEFT));
        binding.bottomRightButton.setOnClickListener(v -> refreshGravityButton(PinTouch.TouchAnchor.BOTTOM_RIGHT));

        handler = new Handler();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtils.getAttrColor(getContext(), R.attr.colorPrimaryLight, 0));
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);

        padding = Math.round(DisplayUtils.dp2px(context, 20));

        refreshGravityButton(pinTouch.getAnchor());
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
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN -> {
                boolean flag = true;
                if (isMarked) {
                    int[] location = new int[2];
                    binding.markBox.getLocationOnScreen(location);
                    Rect rect = new Rect(location[0], location[1], location[0] + binding.markBox.getWidth(), location[1] + binding.markBox.getHeight());
                    if (rect.contains((int) x, (int) y)) {
                        flag = false;
                        lastX = x;
                        lastY = y;
                    }
                }
                if (flag) {
                    isMarked = false;
                    records.clear();
                    lastTime = System.currentTimeMillis();
                    addTouchRecord(event, -1);
                }
            }
            case MotionEvent.ACTION_MOVE -> {
                if (isMarked) {
                    float dx = x - lastX;
                    float dy = y - lastY;
                    offset((int) dx, (int) dy);
                    lastX = x;
                    lastY = y;
                } else {
                    addTouchRecord(event, -1);
                }
            }
            case MotionEvent.ACTION_UP -> {
                if (isMarked) {
                    if (isClick) {
                        toLine();
                    } else {
                        isClick = true;
                        postDelayed(() -> isClick = false, 300);
                    }
                } else {
                    isMarked = true;
                    int pointerId = event.getPointerId(event.getActionIndex());
                    addTouchRecord(event, pointerId);
                    supportLongTouch(null);
                }
            }
            case MotionEvent.ACTION_POINTER_DOWN -> {
                if (!isMarked) addTouchRecord(event, -1);
            }
            case MotionEvent.ACTION_POINTER_UP -> {
                if (!isMarked) {
                    int pointerId = event.getPointerId(event.getActionIndex());
                    addTouchRecord(event, pointerId);
                }
            }
        }
        refreshUI();
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

    private void refreshUI() {
        boolean isInit = true;
        for (ArrayList<Point> points : getPaths()) {
            Rect rect = DisplayUtils.calculatePointArea(points);
            if (isInit) {
                realArea.set(rect);
                isInit = false;
            } else {
                realArea.left = Math.min(rect.left, realArea.left);
                realArea.right = Math.max(rect.right, realArea.right);
                realArea.top = Math.min(rect.top, realArea.top);
                realArea.bottom = Math.max(rect.bottom, realArea.bottom);
            }
        }

        Rect markArea = new Rect(realArea);
        Point size = DisplayUtils.getScreenSize(getContext());
        if (markArea.left < location[0] + padding * 2) markArea.left = location[0] + padding * 2;
        if (markArea.top < location[1] + padding * 2) markArea.top = location[1] + padding * 2;
        if (markArea.right > size.x - padding * 2) markArea.right = size.x - padding * 2;
        if (markArea.bottom > size.y - padding * 2) markArea.bottom = size.y - padding * 2;
        markArea.sort();

        binding.markBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        binding.buttonBox.setVisibility(isMarked ? VISIBLE : INVISIBLE);
        if (isMarked) {
            ViewGroup.LayoutParams params = binding.markBox.getLayoutParams();
            params.width = markArea.width() + 2 * padding;
            params.height = markArea.height() + 2 * padding;
            binding.markBox.setLayoutParams(params);

            binding.markBox.setX(markArea.left - padding - location[0]);
            binding.markBox.setY(markArea.top - padding - location[1]);

            binding.topRightButton.setX(params.width - binding.topRightButton.getWidth());
            binding.bottomLeftButton.setY(params.height - binding.bottomLeftButton.getHeight());
            binding.bottomRightButton.setX(params.width - binding.bottomRightButton.getWidth());
            binding.bottomRightButton.setY(params.height - binding.bottomRightButton.getHeight());

            float x = markArea.left + (markArea.width() - binding.buttonBox.getWidth()) / 2f - location[0];
            x = Math.max(Math.min(x, size.x - binding.buttonBox.getWidth()), 0);
            binding.buttonBox.setX(x);
            if (markArea.bottom + padding * 2 + binding.buttonBox.getHeight() > binding.getRoot().getHeight()) {
                binding.buttonBox.setY(markArea.top - padding * 2 - binding.buttonBox.getHeight() - location[1]);
            } else {
                binding.buttonBox.setY(markArea.bottom + padding * 2 - location[1]);
            }
        }
        postInvalidate();
        if (binding.topRightButton.getWidth() == 0) {
            post(this::refreshUI);
        }
    }

    private void refreshGravityButton(PinTouch.TouchAnchor anchor) {
        this.anchor = anchor;
        binding.topLeftButton.setIconResource(R.drawable.icon_radio_checked);
        binding.topRightButton.setIconResource(R.drawable.icon_radio_checked);
        binding.bottomLeftButton.setIconResource(R.drawable.icon_radio_checked);
        binding.bottomRightButton.setIconResource(R.drawable.icon_radio_checked);
        switch (anchor) {
            case TOP_LEFT -> binding.topLeftButton.setIconResource(R.drawable.icon_radio_unchecked);
            case TOP_RIGHT -> binding.topRightButton.setIconResource(R.drawable.icon_radio_unchecked);
            case BOTTOM_LEFT -> binding.bottomLeftButton.setIconResource(R.drawable.icon_radio_unchecked);
            case BOTTOM_RIGHT -> binding.bottomRightButton.setIconResource(R.drawable.icon_radio_unchecked);
        }
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

    private void toLine() {
        ArrayList<PinTouch.TouchRecord> records = new ArrayList<>();

        int time = 0;
        for (PinTouch.TouchRecord record : this.records) {
            if (records.isEmpty()) records.add(record);
            else {
                time += record.getTime();
                if (record.existEndPoint()) {
                    PinTouch.TouchRecord touchRecord = new PinTouch.TouchRecord(time);
                    record.getPoints().forEach(touchRecord::addPoint);
                    records.add(touchRecord);
                }
            }
        }
        this.records.clear();
        this.records.addAll(records);
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

    private void offset(int x, int y) {
        records.forEach(record -> record.offset(x, y));
    }
}
