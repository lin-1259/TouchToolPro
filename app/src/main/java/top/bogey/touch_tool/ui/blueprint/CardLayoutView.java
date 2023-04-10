package top.bogey.touch_tool.ui.blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
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

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.action.function.FunctionAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinValue;
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
    private PinBaseView<?> matchedPin = null;

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
                scale = Math.max(0.3f, Math.min(scale, 2f));

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
        offsetX = 0;
        offsetY = 0;
        scale = 0.9f;
        cardMap.clear();
        removeAllViews();
        for (BaseAction action : actionContext.getActions()) {
            if (action instanceof BaseFunction) {
                ((BaseFunction) action).sync(actionContext);
            }
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
            Constructor<?> constructor = actionClass.getConstructor();
            BaseAction action = (BaseAction) constructor.newInstance();
            action.x = (int) (-offsetX / getScaleGridSize()) + 1;
            action.y = (int) (-offsetY / getScaleGridSize()) + 1;
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addAction(Class<?> actionClass, String key, PinObject value) {
        try {
            Constructor<?> constructor = actionClass.getConstructor(String.class, PinObject.class);
            BaseAction action = (BaseAction) constructor.newInstance(key, value);
            action.x = (int) (-offsetX / getScaleGridSize()) + 1;
            action.y = (int) (-offsetY / getScaleGridSize()) + 1;
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void addAction(String functionId) {
        BaseFunction function = TaskRepository.getInstance().getFunctionById(functionId);
        if (function != null) {
            function = (BaseFunction) function.copy();
            function.x = (int) (-offsetX / getScaleGridSize()) + 1;
            function.y = (int) (-offsetY / getScaleGridSize()) + 1;
            addAction((BaseAction) function);
        }
    }

    public void addAction(BaseFunction function) {
        if (function != null) {
            function = (BaseFunction) function.copy();
            function.x = (int) (-offsetX / getScaleGridSize()) + 1;
            function.y = (int) (-offsetY / getScaleGridSize()) + 1;
            addAction((BaseAction) function);
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
                gridPaint.setStrokeWidth(6);
            } else {
                float v = (startY - i * gridScaleSize) % bigGridSize;
                gridPaint.setStrokeWidth((Math.abs(v) < 1 || Math.abs(v) > bigGridSize - 1) ? 2 : 0.5f);
            }
            canvas.drawLine(-gridScaleSize, i * gridScaleSize, getWidth() + gridScaleSize, i * gridScaleSize, gridPaint);
        }

        float startX = offsetX - ofX;
        for (int i = 0; i < gridCol; i++) {
            if (offsetX == i * gridScaleSize + ofX) {
                gridPaint.setStrokeWidth(6);
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
                if (matchedPin != null) linePaint.setColor(matchedPin.getPinColor());
                else linePaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryInverse, 0));
                canvas.drawPath(calculateLinePath(pinBaseView), linePaint);
            }
        }

        // 所有卡片
        super.dispatchDraw(canvas);
    }

    //带拐点的路径，尽可能少的拐点
    private Path calculateLinePath(int[] outLocation, int[] inLocation, boolean v) {
        Path path = new Path();
        if (outLocation == null || inLocation == null) return path;
        float scaleGridSize = getScaleGridSize();

        PointF outLinkLinePoint;
        PointF inLinkLinePoint;

        if (v) {
            outLinkLinePoint = new PointF(outLocation[0], outLocation[1] + scaleGridSize);
            inLinkLinePoint = new PointF(inLocation[0], inLocation[1] - scaleGridSize);
        } else {
            outLinkLinePoint = new PointF(outLocation[0] + scaleGridSize, outLocation[1]);
            inLinkLinePoint = new PointF(inLocation[0] - scaleGridSize, inLocation[1]);
        }

        // 结束点在右边，为正方向
        int xScale = outLinkLinePoint.x < inLinkLinePoint.x ? 1 : -1;
        // 结束点在下边，为正方向
        int yScale = outLinkLinePoint.y < inLinkLinePoint.y ? 1 : -1;

        path.moveTo(outLocation[0], outLocation[1]);
        path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y);

        float offsetX = Math.abs(outLinkLinePoint.x - inLinkLinePoint.x);
        float offsetY = Math.abs(outLinkLinePoint.y - inLinkLinePoint.y);
        boolean xLong = offsetX > offsetY;

        /*
        垂直连接：
            X长度为0：
                yScale = 1, 向下连接，看其他条件
                yScale = -1, 向右绕2格连接
            X更长：
                yScale = 1, 就先竖，再横，再竖
                yScale = -1， 就先横，再斜，再横
            Y更长：
                yScale = 1, 就先竖，再斜，再竖
                yScale = -1, 就先横，再竖，再横
        水平连接：
            X更长：
                xScale = 1, 就先横，再斜，再横
                xScale = -1, 就先竖，再横，再竖
            Y更长：
                xScale = 1, 就先横，再竖，再横
                xScale = -1, 就先竖，再斜，再竖
            Y长度为0：
                xScale = 1, 水平连接，看其他条件
                xScale = -1, 向下绕2格连接
        */
        float linkLineLen = Math.abs(offsetX - offsetY) / 2;

        boolean flag = true;
        if (offsetX < scaleGridSize * 3.1 && v) {
            if (offsetX < 1) {
                flag = false;
            } else if (yScale == -1) {
                // 向左绕2格连接
                float x = Math.max(outLinkLinePoint.x, inLinkLinePoint.x) - scaleGridSize * 6;
                path.lineTo(x, outLinkLinePoint.y);
                path.lineTo(x, inLinkLinePoint.y);
                flag = false;
            }
        } else if (offsetY < scaleGridSize * 3.1 && !v) {
            if (offsetY < 1) {
                flag = false;
            } else if (xScale == -1) {
                //向下绕2格连接
                float y = Math.max(outLinkLinePoint.y, inLinkLinePoint.y) + scaleGridSize * 6;
                path.lineTo(outLinkLinePoint.x, y);
                path.lineTo(inLinkLinePoint.x, y);
                flag = false;
            }
        }

        if (flag) {
            if (xLong) {
                if ((v && yScale == 1) || (!v && xScale == -1)) {
                    //就先竖，再横，再竖
                    path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y + offsetY / 2 * yScale);
                    path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y - offsetY / 2 * yScale);
                } else {
                    //就先横，再斜，再横
                    path.lineTo(outLinkLinePoint.x + linkLineLen * xScale, outLinkLinePoint.y);
                    path.lineTo(inLinkLinePoint.x - linkLineLen * xScale, inLinkLinePoint.y);
                }
            } else {
                if ((v && yScale == 1) || (!v && xScale == -1)) {
                    //就先竖，再斜，再竖
                    path.lineTo(outLinkLinePoint.x, outLinkLinePoint.y + linkLineLen * yScale);
                    path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y - linkLineLen * yScale);
                } else {
                    //就先横，再竖，再横
                    path.lineTo(outLinkLinePoint.x + offsetX / 2 * xScale, outLinkLinePoint.y);
                    path.lineTo(inLinkLinePoint.x - offsetX / 2 * xScale, inLinkLinePoint.y);
                }
            }
        }

        path.lineTo(inLinkLinePoint.x, inLinkLinePoint.y);
        path.lineTo(inLocation[0], inLocation[1]);

        path.offset(-location[0] - getX(), -location[1] - getY());

        return path;
    }

    private Path calculateLinePath(PinBaseView<?> outPin, PinBaseView<?> inPin) {
        Path path = new Path();
        if (outPin == null || inPin == null) return path;

        int[] outLocation = outPin.getSlotLocationOnScreen(scale);
        int[] inLocation = inPin.getSlotLocationOnScreen(scale);
        return calculateLinePath(outLocation, inLocation, outPin.getPin().isVertical());
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
        return calculateLinePath(outLocation, inLocation, pinBaseView.getPin().isVertical());
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
                            dragState = DRAG_PIN;
                            HashMap<String, String> links = pin.getLinks();
                            // 数量为0 或者 是出线且可以出多条线，从这个点出线。进线要么连接，要么断开
                            if (links.size() == 0 || (!pin.isSingle() && pin.getDirection().isOut())) {
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

                matchedPin = null;
                for (BaseCard<?> baseCard : cardMap.values()) {
                    int[] location = new int[2];
                    baseCard.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + (int) (baseCard.getWidth() * scale), location[1] + (int) (baseCard.getHeight() * scale)).contains((int) rawX, (int) rawY)) {
                        PinBaseView<?> pinBaseView = baseCard.getPinByPosition(rawX, rawY);
                        if (pinBaseView == null) continue;
                        matchedPin = pinBaseView;
                        break;
                    }
                }
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
        return pin.addLinks(actionContext, links);
    }

    private void pinRemoveLinks(PinBaseView<?> pinBaseView) {
        Pin pin = pinBaseView.getPin();
        pin.removeLinks(actionContext);
    }

    public void refreshActionPins(BaseAction action) {
        BaseCard<?> baseCard = cardMap.get(action.getId());
        if (baseCard == null) return;
        for (Pin pin : action.getPins()) {
            if (pin.getValue() instanceof PinValue) {
                pin.removeLinks(actionContext);
                PinBaseView<?> pinById = baseCard.getPinById(pin.getId());
                pinById.setValueView();
                pinById.refreshPinUI();
            }
        }
        postInvalidate();
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
