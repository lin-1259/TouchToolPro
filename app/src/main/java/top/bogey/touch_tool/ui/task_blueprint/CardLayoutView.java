package top.bogey.touch_tool.ui.task_blueprint;

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

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSlotType;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.ui.card.BaseCard;
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

    private Task task;

    private int dragState = DRAG_NONE;
    private final HashMap<String, String> dragLinks = new HashMap<>();
    private BaseCard<?> dragCard = null;
    private PinBaseView<?> dragPin = null;
    private float dragX = 0;
    private float dragY = 0;
    private PinDirection dragDirection;

    private float offsetX = 0;
    private float offsetY = 0;

    private float scale = 1f;
    private final ScaleGestureDetector detector;

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gridSize = DisplayUtils.dp2px(context, 8);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(1);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setColor(context.getColor(R.color.IntegerPinColor));
        gridPaint.setAlpha(20);

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
                scale *= detector.getScaleFactor();
                scale = Math.max(0.5f, Math.min(scale, 1.5f));
                setCardsPosition();
                postInvalidate();
                return true;
            }
        });
    }

    public void setTask(Task task) {
        this.task = task;
        cardMap.clear();
        removeAllViews();
        for (BaseAction action : task.getActions()) {
            BaseCard<?> card = new BaseCard<>(getContext(), task, action);
            setCardPosition(card);
            addView(card);
            cardMap.put(action.getId(), card);
        }
    }

    public void addAction(BaseAction action) {
        task.addAction(action);
        BaseCard<?> card = new BaseCard<>(getContext(), task, action);
        setCardPosition(card);
        addView(card);
        cardMap.put(action.getId(), card);
    }

    public void addAction(Class<?> actionClass) {
        try {
            Constructor<?> constructor = actionClass.getConstructor(Context.class);
            BaseAction action = (BaseAction) constructor.newInstance(getContext());
            action.x = (int) (-offsetX / gridSize) + 1;
            action.y = (int) (-offsetY / gridSize) + 1;
            addAction(action);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAction(BaseAction action) {
        task.removeAction(action);
        BaseCard<?> card = cardMap.remove(action.getId());
        if (card == null) return;
        for (Pin pin : action.getPins()) {
            linksRemovePin(pin.getLinks(), card.getPinById(pin.getId()));
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
        card.setX(action.x * gridSize * scale + offsetX);
        card.setY(action.y * gridSize * scale + offsetY);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(offsetX % (gridSize / scale), offsetY % (gridSize / scale));
        canvas.scale(scale, scale);

        // 格子背景
        for (int i = 0; i < (getWidth() / scale / gridSize); i++) {
            gridPaint.setStrokeWidth(i == 0 ? 2 : 1);
            canvas.drawLine(i * gridSize, 0, i * gridSize, getHeight() / scale, gridPaint);
        }

        for (int i = 0; i < (getHeight() / scale / gridSize); i++) {
            gridPaint.setStrokeWidth(i == 0 ? 2 : 1);
            canvas.drawLine(0, i * gridSize, getWidth() / scale, i * gridSize, gridPaint);
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
                    if (pinBaseView.getPin().getDirection() == PinDirection.OUT) {
                        linePaint.setColor(pinBaseView.getPin().getPinColor(getContext()));
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
                linePaint.setColor(pinBaseView.getPin().getPinColor(getContext()));
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

        // 执行是上下的
        if (outPin.getPin().getPinClass().isAssignableFrom(PinExecute.class)) {
            int offset = inLocation[1] - outLocation[1];
            offset = offset > gridSize ? offset : gridSize * 8;
            int y1 = outLocation[1] + offset;
            int y2 = inLocation[1] - offset;
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(outLocation[0], y1, inLocation[0], y2, inLocation[0], inLocation[1]);
        } else {
            int offset = inLocation[0] - outLocation[0];
            offset = offset > gridSize ? offset : gridSize * 8;
            int x1 = outLocation[0] + offset;
            int x2 = inLocation[0] - offset;
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

        // 执行是上下的
        if (pinBaseView.getPin().getPinClass().isAssignableFrom(PinExecute.class)) {
            int offset = inLocation[1] - outLocation[1];
            offset = offset > gridSize ? offset : gridSize * 8;
            int y1 = outLocation[1] + offset;
            int y2 = inLocation[1] - offset;
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(outLocation[0], y1, inLocation[0], y2, inLocation[0], inLocation[1]);
        } else {
            int offset = inLocation[0] - outLocation[0];
            offset = offset > gridSize ? offset : gridSize * 8;
            int x1 = outLocation[0] + offset;
            int x2 = inLocation[0] - offset;
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
                            if (links.size() == 0 || (pin.getSlotType() == PinSlotType.MULTI && pin.getDirection() == PinDirection.OUT)) {
                                dragLinks.put(pin.getId(), pin.getActionId());
                                // 目标方向与自身相反
                                dragDirection = pin.getDirection() == PinDirection.IN ? PinDirection.OUT : PinDirection.IN;
                                dragPin = pinBaseView;
                            } else {
                                // 否则就是挪线
                                dragLinks.putAll(links);
                                dragDirection = pin.getDirection();
                                linksRemovePin(links, pinBaseView);
                                links.clear();
                                pinBaseView.refreshPinUI();
                            }
                        }
                    }
                    dragX = rawX;
                    dragY = rawY;
                    break;
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
                if (flag && Math.abs(rawX - dragX) * Math.abs(rawY - dragY) <= 81) {
                    // 无效的拖动且没怎么拖动，相当于点击了这个针脚，点击针脚是断开这个针脚
                    if (dragPin != null) {
                        HashMap<String, String> links = dragPin.getPin().getLinks();
                        linksRemovePin(links, dragPin);
                        links.clear();
                        dragPin.refreshPinUI();
                    }
                }
            }

            dragLinks.clear();
            dragCard = null;
            dragPin = null;
            dragState = DRAG_NONE;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (dragState == DRAG_CARD) {
                BaseAction action = dragCard.getAction();
                int dx = (int) ((rawX - dragX) / gridSize);
                if (dx != 0) {
                    action.x += dx;
                    dragX += dx * gridSize;
                }
                int dy = (int) ((rawY - dragY) / gridSize);
                if (dy != 0) {
                    action.y += dy;
                    dragY += dy * gridSize;
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
        boolean flag = true;
        // 先判断一下针脚是否匹配
        for (Map.Entry<String, String> entry : links.entrySet()) {
            BaseAction action = task.getActionById(entry.getValue());
            if (action == null) continue;
            Pin linkPin = action.getPinById(entry.getKey());
            if (!pin.getPinClass().isAssignableFrom(linkPin.getPinClass())) {
                flag = false;
                break;
            }

            if (pin.getDirection() == linkPin.getDirection()) {
                flag = false;
                break;
            }
        }

        if (flag) {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                BaseCard<?> card = cardMap.get(entry.getValue());
                if (card == null) continue;
                PinBaseView<?> cardPin = card.getPinById(entry.getKey());
                if (cardPin == null) continue;
                // 不能自己首尾相连
                if (pin.getActionId().equals(cardPin.getPin().getActionId())) continue;

                linksRemovePin(cardPin.addLink(pin), cardPin);
                linksRemovePin(pinBaseView.addLink(cardPin.getPin()), pinBaseView);
            }
        }
        return flag;
    }

    public void linksRemovePin(HashMap<String, String> links, PinBaseView<?> pinBaseView) {
        for (Map.Entry<String, String> entry : links.entrySet()) {
            BaseCard<?> card = cardMap.get(entry.getValue());
            if (card == null) continue;
            PinBaseView<?> cardPin = card.getPinById(entry.getKey());
            if (cardPin == null) continue;
            cardPin.removeLink(pinBaseView.getPin());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getLocationOnScreen(location);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TaskRepository.getInstance().saveTask(task);
    }
}
