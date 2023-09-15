package top.bogey.touch_tool_pro.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

import top.bogey.touch_tool_pro.utils.DisplayUtils;

public class TouchPathView extends View {
    private final HashSet<ArrayList<Point>> paths = new HashSet<>();

    private Paint paint;
    private final Point size = new Point();
    private final int lineWidth = 5;
    private boolean isInit = true;

    public TouchPathView(Context context) {
        super(context);
        init();
    }

    public TouchPathView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0));
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        size.set(getWidth() - lineWidth * 2, getHeight() - lineWidth * 2);
        if (isInit) postInvalidate();
        isInit = false;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);

        for (ArrayList<Point> pointPath : paths) {
            ArrayList<Point> points = formatPoints(pointPath);

            if (points.size() >= 2) {
                Path path = new Path();
                for (Point point : points) {
                    if (path.isEmpty()) path.moveTo(point.x, point.y);
                    else path.lineTo(point.x, point.y);
                }
                canvas.drawPath(path, paint);
            }

            if (points.size() >= 1) {
                Point point = points.get(points.size() - 1);
                canvas.drawCircle(point.x, point.y, 3, paint);
            }
        }
    }

    public void setPaths(HashSet<ArrayList<Point>> paths) {
        this.paths.clear();
        if (paths != null) this.paths.addAll(paths);
        postInvalidate();
    }

    public ArrayList<Point> formatPoints(ArrayList<Point> points) {
        Rect area = new Rect();
        boolean init = false;
        for (ArrayList<Point> pointPath : paths) {
            Rect rect = DisplayUtils.calculatePointArea(pointPath);
            if (!init) {
                area.set(rect);
                init = true;
            }
            else {
                area.left = Math.min(rect.left, area.left);
                area.right = Math.max(rect.right, area.right);
                area.top = Math.min(rect.top, area.top);
                area.bottom = Math.max(rect.bottom, area.bottom);
            }
        }

        float xScale, yScale, xOffset, yOffset;

        if (area.width() == 0) {
            xScale = 1;
        } else {
            xScale = size.x * 1f / area.width();
        }

        if (area.height() == 0) {
            yScale = 1;
        } else {
            yScale = size.y * 1f / area.height();
        }

        float scale = Math.min(xScale, yScale);
        xOffset = (size.x - area.width() * scale) / 2f;
        yOffset = (size.y - area.height() * scale) / 2f;

        ArrayList<Point> showPoints = new ArrayList<>();
        for (Point point : points) {
            int x = point.x - area.left;
            int y = point.y - area.top;
            x = (int) (x * scale + xOffset);
            y = (int) (y * scale + yOffset);
            showPoints.add(new Point(x + lineWidth, y + lineWidth));
        }
        return showPoints;
    }
}
