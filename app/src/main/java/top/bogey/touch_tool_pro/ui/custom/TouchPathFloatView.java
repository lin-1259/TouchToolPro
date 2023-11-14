package top.bogey.touch_tool_pro.ui.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;
import top.bogey.touch_tool_pro.utils.easy_float.FloatGravity;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewHelper;
import top.bogey.touch_tool_pro.utils.easy_float.FloatViewInterface;

@SuppressLint("ViewConstructor")
public class TouchPathFloatView extends AppCompatImageView implements FloatViewInterface {
    private final String tag;
    private final PinTouch touch;
    private final float timeScale;
    private final Paint paint;

    private final int lineWidth = 10;
    private final int paddingScale = 4;
    private final int padding = lineWidth * paddingScale / 2;
    private final HashMap<Integer, Point> lastTouch = new HashMap<>();
    private final ArrayList<PinTouch.TouchRecord> records;
    private Canvas canvas = null;
    private int index;

    public TouchPathFloatView(@NonNull Context context, PinTouch touch, float scale) {
        super(context);
        this.touch = touch;
        timeScale = scale;

        records = touch.getRecords();
        tag = UUID.randomUUID().toString();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(DisplayUtils.getAttrColor(getContext(), R.attr.colorPrimaryLight, 0));
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Rect area = touch.getRecordsArea(getContext());
        int width = area.width() + paddingScale * lineWidth;
        int height = area.height() + paddingScale * lineWidth;
        setMeasuredDimension(width, height);

        if (canvas == null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            @SuppressLint("DrawAllocation") int[] location = new int[2];
            getLocationOnScreen(location);
            FloatViewHelper helper = EasyFloat.getHelper(tag);
            helper.offset(-location[0], -location[1]);
            helper.initGravity();
        }
        if (index == 0) {
            index++;
            startAni();
        }
    }

    private void startAni() {
        if (records.size() == 1) {
            PinTouch.TouchRecord record = records.get(0);
            paint.setStrokeWidth(lineWidth * 2);
            record.getPoints().forEach(point -> canvas.drawCircle(point.x + padding, point.y + padding, lineWidth, paint));
            postDelayed(() -> animate().alpha(0).withEndAction(this::dismiss), record.getTime());
        } else {
            int time = 0;
            for (PinTouch.TouchRecord record : records) {
                time += record.getTime();
            }

            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration((long) (time * timeScale));
            animator.addUpdateListener(animation -> {
                float now = animation.getCurrentPlayTime() / timeScale;
                int index = 0;
                int total = 0;
                float percent = 0;
                for (int i = 0; i < records.size(); i++) {
                    PinTouch.TouchRecord record = records.get(i);
                    if (total + record.getTime() > now) {
                        index = i;
                        percent = (now - total) / record.getTime();
                        break;
                    }
                    total += record.getTime();
                }

                if (index > 0) {
                    PinTouch.TouchRecord lastRecord = records.get(index - 1);
                    PinTouch.TouchRecord record = records.get(index);
                    float value = percent;
                    record.getPoints().forEach(point -> {
                        PinTouch.PathPoint lastPathPoint = lastRecord.getPointByOwnerId(point.getOwnerId());
                        if (lastPathPoint == null) {
                            return;
                        }
                        Point lastPoint = lastTouch.computeIfAbsent(point.getOwnerId(), k -> new Point(lastPathPoint.x + padding, lastPathPoint.y + padding));
                        int x = (int) ((point.x - lastPathPoint.x) * value + lastPathPoint.x + padding);
                        int y = (int) ((point.y - lastPathPoint.y) * value + lastPathPoint.y + padding);
                        paint.setStrokeWidth(lineWidth);
                        canvas.drawLine(lastPoint.x, lastPoint.y, x, y, paint);
                        lastPoint.set(x, y);
                    });
                }
                invalidate();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animate().alpha(0).withEndAction(() -> dismiss());
                }
            });

            animator.start();
        }
    }

    @Override
    public void show() {
        Point point = touch.getAnchorPoint(getContext());

        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setGravity(FloatGravity.TOP_LEFT, point.x - padding, point.y - padding)
                .setDragEnable(false)
                .setAnimator(null)
                .setFlag(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                .show();
    }

    @Override
    public void dismiss() {
        EasyFloat.dismiss(tag);
    }
}
