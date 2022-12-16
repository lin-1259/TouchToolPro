package top.bogey.touch_tool.ui.task_build;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.utils.DisplayUtils;

public class CardLayoutView extends FrameLayout {
    private static final int DRAG_NONE = 0;
    private static final int DRAG_SELF = 1;
    private static final int DRAG_CARD = 2;
    private static final int DRAG_LINE = 3;

    private final int gridSize;
    private final Paint eventPaint;
    private final Paint statePaint;
    private final Paint dragPaint;

    private final Map<String, BaseCard<? extends BaseAction>> cardMap = new HashMap<>();

    private Task task;
    private int dragState = DRAG_NONE;

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gridSize = DisplayUtils.dp2px(context, 8);

        eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eventPaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0));
        eventPaint.setStrokeWidth(10);
        eventPaint.setStrokeCap(Paint.Cap.ROUND);
        eventPaint.setStrokeJoin(Paint.Join.ROUND);
        eventPaint.setStyle(Paint.Style.STROKE);

        statePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        statePaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryInverse, 0));
        statePaint.setStrokeWidth(10);
        statePaint.setStrokeCap(Paint.Cap.ROUND);
        statePaint.setStrokeJoin(Paint.Join.ROUND);
        statePaint.setStyle(Paint.Style.STROKE);

        dragPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dragPaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorOutline, 0));
        dragPaint.setStrokeWidth(10);
        dragPaint.setStrokeCap(Paint.Cap.ROUND);
        dragPaint.setStrokeJoin(Paint.Join.ROUND);
        dragPaint.setStyle(Paint.Style.STROKE);
    }

    public void setTask(Task task) {
        this.task = task;
        cardMap.clear();
        removeAllViews();
        for (BaseAction action : task.getActions()) {
            BaseCard<? extends BaseAction> card = new BaseCard<>(getContext(), task, action);
            setCardPosition(card);
            addView(card);
            cardMap.put(action.getId(), card);
        }
    }

    private void setCardPosition(BaseCard<? extends BaseAction> card) {
        BaseAction action = card.getAction();
        card.setX(action.x * gridSize);
        card.setY(action.y * gridSize);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 所有卡片
        super.dispatchDraw(canvas);

        // 所有连接的线
        for (BaseCard<? extends BaseAction> card : cardMap.values()) {
            BaseAction action = card.getAction();
            if (action == null) continue;

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
