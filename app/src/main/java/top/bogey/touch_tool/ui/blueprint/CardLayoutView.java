package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.action.function.FunctionAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.card.custom.CustomCard;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.utils.DisplayUtils;

public class CardLayoutView extends FrameLayout {
    private static final int DRAG_NONE = 0;
    private static final int DRAG_SELF = 1;
    private static final int DRAG_CARD = 2;
    private static final int DRAG_PIN = 3;
    private static final int DRAG_SCALE = 4;

    private final int gridSize;
    private final Paint gridPaint;
    private final Paint linePaint;
    private final int[] location = new int[2];

    private final HashMap<String, BaseCard<?>> cardMap = new LinkedHashMap<>();
    private ActionContext actionContext;

    private int dragState = DRAG_NONE;
    private final HashMap<String, String> dragLinks = new HashMap<>();
    private BaseCard<?> dragCard = null;
    private PinBaseView<?> dragPin = null;
    private float dragX = 0;
    private float dragY = 0;
    private float startX = 0;
    private float startY = 0;
    private PinDirection dragDirection;

    private float offsetX = 0;
    private float offsetY = 0;

    private float scale = 1f;
    private final ScaleGestureDetector detector;

    private boolean editMode = true;

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gridSize = DisplayUtils.dp2px(context, 8);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimary, 0));
        gridPaint.setAlpha(40);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(5);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStyle(Paint.Style.STROKE);

        detector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                dragState = DRAG_SCALE;
                return true;
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                dragState = DRAG_NONE;
            }

            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                float oldScale = scale;
                scale *= detector.getScaleFactor();
                scale = Math.max(0.5f, Math.min(scale, 1.5f));

                // 设置居中缩放偏移
                float v = 1 - scale / oldScale;
                float focusX = detector.getFocusX() - offsetX;
                float focusY = detector.getFocusY() - offsetY;
                offsetX += focusX * v;
                offsetY += focusY * v;

                setCardsPosition();
                postInvalidate();
                return true;
            }
        });
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public BaseCard<?> newCard(ActionContext actionContext, BaseAction action) {
        if (action instanceof FunctionAction) {
            return new CustomCard(getContext(), (BaseFunction) actionContext, (FunctionAction) action);
        }
        return new BaseCard<>(getContext(), actionContext, action);
    }

    public void setActionContext(ActionContext actionContext) {
        this.actionContext = actionContext;
        cardMap.clear();
        removeAllViews();
        for (BaseAction action : actionContext.getActions()) {
            BaseCard<?> card = newCard(actionContext, action);
            setCardPosition(card);
            addView(card);
            cardMap.put(action.getId(), card);
        }
    }

    public void addAction(BaseAction action) {
        actionContext.addAction(action);
        BaseCard<?> card = newCard(actionContext, action);
        setCardPosition(card);
        addView(card);
        cardMap.put(action.getId(), card);
    }

    public void addAction(Class<?> actionClass) {
        try {
            Constructor<?> constructor = actionClass.getConstructor(Context.class);
            BaseAction action = (BaseAction) constructor.newInstance(getContext());
            action.x = (int) (-offsetX / getScaleGridSize()) + 1;
            action.y = (int) (-offsetY / getScaleGridSize()) + 1;
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAction(String functionId) {
        BaseFunction function = TaskRepository.getInstance().getFunctionById(functionId);
        if (function != null) {
            function = (BaseFunction) function.copy();
            function.x = (int) (-offsetX / getScaleGridSize()) + 1;
            function.y = (int) (-offsetY / getScaleGridSize()) + 1;
            addAction(function);
        }
    }

    public void removeAction(BaseAction action) {
        actionContext.removeAction(action);
        BaseCard<?> card = cardMap.remove(action.getId());
        if (card == null) return;
        for (Pin pin : action.getShowPins()) {
            pinRemoveLinks(card.getPinById(pin.getId()));
        }

        removeView(card);
    }

    private void setCardsPosition() {
        for (BaseCard<?> baseCard : cardMap.values()) {
            setCardPosition(baseCard);
        }
    }

    private void setCardPosition(BaseCard<?> card) {
        BaseAction action = card.getAction();
        card.setScaleX(scale);
        card.setScaleY(scale);
        card.setX(action.x * getScaleGridSize() + offsetX);
        card.setY(action.y * getScaleGridSize() + offsetY);
    }

    private float getScaleGridSize() {
        return gridSize * scale;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        float gridScaleSize = getScaleGridSize();
        float ofX = offsetX % gridScaleSize;
        float ofY = offsetY % gridScaleSize;
        canvas.translate(ofX, ofY);

        // 格子背景
        float gridRow = getHeight() / gridScaleSize; //有多少行
        float gridCol = getWidth() / gridScaleSize;  //有多少列

        float bigGridSize = 10 * gridScaleSize;
        float startY = offsetY - ofY;
        for (int i = 0; i < gridRow; i++) {
            if (startY == i * gridScaleSize) {
                gridPaint.setStrokeWidth(4);
            } else {
                float v = (startY - i * gridScaleSize) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(-gridScaleSize, i * gridScaleSize, getWidth() + gridScaleSize, i * gridScaleSize, gridPaint);
        }

        float startX = offsetX - ofX;
        for (int i = 0; i < gridCol; i++) {
            if (offsetX == i * gridScaleSize + ofX) {
                gridPaint.setStrokeWidth(4);
            } else {
                float v = (startX - i * gridScaleSize) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(i * gridScaleSize, -gridScaleSize, i * gridScaleSize, getHeight() + gridScaleSize, gridPaint);
        }
        canvas.restore();

        // 所有连接的线
        for (BaseCard<?> card : cardMap.values()) {
            BaseAction action = card.getAction();
            for (Pin pin : action.getPins()) {
                for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                    BaseCard<?> baseCard = cardMap.get(entry.getValue());
                    if (baseCard == null) continue;
                    PinBaseView<?> pinBaseView = baseCard.getPinById(entry.getKey());
                    if (pinBaseView == null) continue;
                    // 只画输出的线
                    if (pinBaseView.getPin().getDirection().isOut()) {
                        linePaint.setColor(pinBaseView.getPinColor());
                        canvas.drawPath(calculateLinePath(pinBaseView, card.getPinById(pin.getId())), linePaint);
                    }
                }
            }
        }

        if (dragState == DRAG_PIN) {
            for (Map.Entry<String, String> entry : dragLinks.entrySet()) {
                BaseCard<?> card = cardMap.get(entry.getValue());
                if (card == null) continue;
                PinBaseView<?> pinBaseView = card.getPinById(entry.getKey());
                if (pinBaseView == null) continue;
                linePaint.setColor(pinBaseView.getPinColor());
                canvas.drawPath(calculateLinePath(pinBaseView), linePaint);
            }
        }

        // 所有卡片
        super.dispatchDraw(canvas);
    }

    private Path calculateLinePath(PinBaseView<?> outPin, PinBaseView<?> inPin) {
        Path path = new Path();
        if (outPin == null || inPin == null) return path;

        int[] outLocation = outPin.getSlotLocationOnScreen(scale);
        int[] inLocation = inPin.getSlotLocationOnScreen(scale);
        float scaleGridSize = getScaleGridSize();

        // 执行是上下的
        if (outPin.getPin().getPinClass().isAssignableFrom(PinExecute.class)) {
            float offset = inLocation[1] - outLocation[1];
            offset = offset > scaleGridSize ? offset : scaleGridSize * 8;
            float y1 = outLocation[1] + offset;
            float y2 = inLocation[1] - offset;
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(outLocation[0], y1, inLocation[0], y2, inLocation[0], inLocation[1]);
        } else {
            float offset = inLocation[0] - outLocation[0];
            offset = offset > scaleGridSize ? offset : scaleGridSize * 8;
            float x1 = outLocation[0] + offset;
            float x2 = inLocation[0] - offset;
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(x1, outLocation[1], x2, inLocation[1], inLocation[0], inLocation[1]);
        }
        path.offset(-location[0] - getX(), -location[1] - getY());

        return path;
    }

    private Path calculateLinePath(PinBaseView<?> pinBaseView) {
        Path path = new Path();
        if (pinBaseView == null) return path;
        int[] pinLocation = pinBaseView.getSlotLocationOnScreen(scale);

        int[] outLocation, inLocation;
        if (dragDirection == PinDirection.OUT) {
            inLocation = pinLocation;
            outLocation = new int[]{(int) dragX, (int) dragY};
        } else {
            inLocation = new int[]{(int) dragX, (int) dragY};
            outLocation = pinLocation;
        }

        float scaleGridSize = getScaleGridSize();
        // 执行是上下的
        if (pinBaseView.getPin().getPinClass().isAssignableFrom(PinExecute.class)) {
            float offset = inLocation[1] - outLocation[1];
            offset = offset > scaleGridSize ? offset : scaleGridSize * 8;
            float y1 = outLocation[1] + offset;
            float y2 = inLocation[1] - offset;
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(outLocation[0], y1, inLocation[0], y2, inLocation[0], inLocation[1]);
        } else {
            float offset = inLocation[0] - outLocation[0];
            offset = offset > scaleGridSize ? offset : scaleGridSize * 8;
            float x1 = outLocation[0] + offset;
            float x2 = inLocation[0] - offset;
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(x1, outLocation[1], x2, inLocation[1], inLocation[0], inLocation[1]);
        }
        path.offset(-location[0] - getX(), -location[1] - getY());
        return path;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (dragState == DRAG_SCALE) return true;

        float rawX = event.getRawX();
        float rawY = event.getRawY();
        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            startX = rawX;
            startY = rawY;
            if (editMode) {
                ArrayList<BaseCard<?>> baseCards = new ArrayList<>(cardMap.values());
                for (int i = baseCards.size() - 1; i >= 0; i--) {
                    BaseCard<?> card = baseCards.get(i);
                    int[] location = new int[2];
                    card.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + (int) (card.getWidth() * scale), location[1] + (int) (card.getHeight() * scale)).contains((int) rawX, (int) rawY)) {
                        dragState = DRAG_CARD;
                        dragCard = card;
                        PinBaseView<?> pinBaseView = card.getPinByPosition(rawX, rawY);
                        if (pinBaseView != null) {
                            Pin pin = pinBaseView.getPin();
                            if (pin.getSlotType() != PinSlotType.EMPTY) {
                                dragState = DRAG_PIN;
                                HashMap<String, String> links = pin.getLinks();
                                // 数量为0 或者 是出线且可以出多条线，从这个点出线。进线要么连接，要么断开
                                if (links.size() == 0 || (pin.getSlotType() == PinSlotType.MULTI && pin.getDirection().isOut())) {
                                    dragLinks.put(pin.getId(), pin.getActionId());
                                    // 目标方向与自身相反
                                    dragDirection = pin.getDirection() == PinDirection.IN ? PinDirection.OUT : PinDirection.IN;
                                    dragPin = pinBaseView;
                                } else {
                                    // 否则就是挪线
                                    dragLinks.putAll(links);
                                    dragDirection = pin.getDirection();
                                    pinRemoveLinks(pinBaseView);
                                }
                            }
                        }
                        dragX = rawX;
                        dragY = rawY;
                        break;
                    }
                }
            }
            if (dragState == DRAG_NONE) {
                dragState = DRAG_SELF;
                dragX = rawX;
                dragY = rawY;
            }
        } else if (actionMasked == MotionEvent.ACTION_UP) {
            if (dragState == DRAG_PIN) {
                boolean flag = true;
                // 看是否放到针脚上了
                for (BaseCard<?> baseCard : cardMap.values()) {
                    int[] location = new int[2];
                    baseCard.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + (int) (baseCard.getWidth() * scale), location[1] + (int) (baseCard.getHeight() * scale)).contains((int) rawX, (int) rawY)) {
                        PinBaseView<?> pinBaseView = baseCard.getPinByPosition(rawX, rawY);
                        if (pinBaseView == null) continue;
                        if (pinAddLinks(pinBaseView, dragLinks)) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag && Math.abs(rawX - startX) * Math.abs(rawY - startY) <= 81) {
                    // 无效的拖动且没怎么拖动，相当于点击了这个针脚，点击针脚是断开这个针脚
                    if (dragPin != null) {
                        pinRemoveLinks(dragPin);
                    }
                }
            }

            dragLinks.clear();
            dragCard = null;
            dragPin = null;
            dragState = DRAG_NONE;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (dragState == DRAG_CARD) {
                float scaleGridSize = getScaleGridSize();
                BaseAction action = dragCard.getAction();
                int dx = (int) ((rawX - dragX) / scaleGridSize);
                if (dx != 0) {
                    action.x += dx;
                    dragX += dx * scaleGridSize;
                }
                int dy = (int) ((rawY - dragY) / scaleGridSize);
                if (dy != 0) {
                    action.y += dy;
                    dragY += dy * scaleGridSize;
                }
                setCardPosition(dragCard);
            } else if (dragState == DRAG_PIN) {
                dragX = rawX;
                dragY = rawY;
                int width = getWidth();
                int height = getHeight();
                int offset = gridSize;
                int areaSize = gridSize * 4;
                if (rawX - location[0] < areaSize) {
                    offsetX += offset;
                } else if (rawX - location[0] > width - areaSize) {
                    offsetX -= offset;
                }
                if (rawY - location[1] < areaSize) {
                    offsetY += offset;
                } else if (rawY - location[1] > height - areaSize) {
                    offsetY -= offset;
                }
                setCardsPosition();
            } else if (dragState == DRAG_SELF) {
                offsetX += (rawX - dragX);
                offsetY += (rawY - dragY);
                dragX = rawX;
                dragY = rawY;
                setCardsPosition();
            }
        }
        postInvalidate();
        return true;
    }

    private boolean pinAddLinks(PinBaseView<?> pinBaseView, HashMap<String, String> links) {
        Pin pin = pinBaseView.getPin();
        HashMap<String, String> addedLinks = pin.addLinks(actionContext, links);
        return addedLinks.size() > 0;
    }

    private void pinRemoveLinks(PinBaseView<?> pinBaseView) {
        Pin pin = pinBaseView.getPin();
        pin.removeLinks(actionContext);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getLocationOnScreen(location);
        }
    }

    public ActionContext getActionContext() {
        return actionContext;
    }
}
