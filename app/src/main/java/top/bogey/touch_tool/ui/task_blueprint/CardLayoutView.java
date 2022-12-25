package top.bogey.touch_tool.ui.task_blueprint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinDirection;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.card.pin.BasePin;
import top.bogey.touch_tool.utils.DisplayUtils;

public class CardLayoutView extends FrameLayout {
    private static final int DRAG_NONE = 0;
    private static final int DRAG_SELF = 1;
    private static final int DRAG_CARD = 2;
    private static final int DRAG_PIN = 3;

    private final int gridSize;
    private final Paint linePaint;
    private final int[] location = new int[2];
    private final Map<String, BaseCard<? extends BaseAction>> cardMap = new LinkedHashMap<>();

    private Task task;

    private int dragState = DRAG_NONE;
    private final Map<String, String> links = new HashMap<>();
    private BaseCard<? extends BaseAction> dragCard = null;
    private float dragX = 0;
    private float dragY = 0;
    private PinDirection dragDirection;

    private float offsetX = 0;
    private float offsetY = 0;

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gridSize = DisplayUtils.dp2px(context, 16);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(10);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStyle(Paint.Style.STROKE);
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

    public void addAction(BaseAction action) {
        task.addAction(action);
        BaseCard<? extends BaseAction> card = new BaseCard<>(getContext(), task, action);
        setCardPosition(card);
        addView(card);
        cardMap.put(action.getId(), card);
    }

    private void setCardsPosition() {
        for (BaseCard<? extends BaseAction> baseCard : cardMap.values()) {
            setCardPosition(baseCard);
        }
    }

    private void setCardPosition(BaseCard<? extends BaseAction> card) {
        BaseAction action = card.getAction();
        card.setX(action.x * gridSize + offsetX);
        card.setY(action.y * gridSize + offsetY);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // 所有连接的线
        for (BaseCard<? extends BaseAction> card : cardMap.values()) {
            BaseAction action = card.getAction();
            for (Pin<?> pin : action.getPins()) {
                for (Map.Entry<String, String> entry : pin.getLinks().entrySet()) {
                    BaseCard<? extends BaseAction> baseCard = cardMap.get(entry.getKey());
                    if (baseCard == null) continue;
                    BasePin<?> basePin = baseCard.getPinById(entry.getValue());
                    if (basePin == null) continue;
                    // 只画输出的线
                    if (basePin.getPin().getDirection() == PinDirection.OUT) {
                        linePaint.setColor(pin.getValue().getPinColor(getContext()));
                        canvas.drawPath(calculateLinePath(basePin, card.getPinById(pin.getId())), linePaint);
                    }
                }
            }
        }
        if (dragState == DRAG_PIN) {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                BaseCard<? extends BaseAction> card = cardMap.get(entry.getKey());
                if (card == null) continue;
                BasePin<?> basePin = card.getPinById(entry.getValue());
                if (basePin == null) continue;
                linePaint.setColor(basePin.getPin().getValue().getPinColor(getContext()));
                canvas.drawPath(calculateLinePath(basePin), linePaint);
            }
        }

        // 所有卡片
        super.dispatchDraw(canvas);
    }

    private Path calculateLinePath(BasePin<?> outPin, BasePin<?> inPin) {
        Path path = new Path();
        if (outPin == null || inPin == null) return path;
        int[] outLocation = outPin.getSlotLocationOnScreen();
        path.moveTo(outLocation[0], outLocation[1]);
        int[] inLocation = inPin.getSlotLocationOnScreen();
        path.cubicTo(outLocation[0] + gridSize * 5, outLocation[1], inLocation[0] - gridSize * 5, inLocation[1], inLocation[0], inLocation[1]);
        path.offset(-location[0] - getX(), -location[1] - getY());
        return path;
    }

    private Path calculateLinePath(BasePin<?> basePin) {
        Path path = new Path();
        if (basePin == null) return path;
        int[] outLocation = basePin.getSlotLocationOnScreen();
        outLocation[0] -= location[0];
        outLocation[1] -= location[1];
        if (dragDirection == PinDirection.IN) {
            path.moveTo(outLocation[0], outLocation[1]);
            path.cubicTo(outLocation[0] + gridSize * 5, outLocation[1], dragX - gridSize * 5, dragY, dragX, dragY);
        } else {
            path.moveTo(dragX, dragY);
            path.cubicTo(dragX + gridSize * 5, dragY, outLocation[0] - gridSize * 5, outLocation[1], outLocation[0], outLocation[1]);
        }
        path.offset(-getX(), -getY());
        return path;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        float x = event.getX();
        float y = event.getY();
        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            links.clear();
            ArrayList<BaseCard<? extends BaseAction>> baseCards = new ArrayList<>(cardMap.values());
            for (int i = baseCards.size() - 1; i >= 0; i--) {
                BaseCard<? extends BaseAction> card = baseCards.get(i);
                int[] location = new int[2];
                card.getLocationOnScreen(location);
                if (new Rect(location[0], location[1], location[0] + card.getWidth(), location[1] + card.getHeight()).contains((int) rawX, (int) rawY)) {
                    dragState = DRAG_CARD;
                    dragCard = card;
                    BasePin<?> basePin = card.getPinByPosition(rawX, rawY);
                    if (basePin != null) {
                        dragState = DRAG_PIN;
                        Pin<?> pinData = basePin.getPin();
                        Map<String, String> map = pinData.getLinks();
                        if (map.size() == 0 || pinData.getDirection() == PinDirection.OUT) {
                            // 数量为0 或者 是出点 且 可以出多条线，从这个点出线
                            links.put(pinData.getActionId(), pinData.getId());
                            // 目标方向与自身相反
                            dragDirection = pinData.getDirection() == PinDirection.IN ? PinDirection.OUT : PinDirection.IN;
                        } else {
                            // 否则就是挪线
                            links.putAll(map);
                            dragDirection = pinData.getDirection();
                            // 挪线需要先把之前的连接断开
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                BaseAction action = task.getActionById(entry.getKey());
                                if (action == null) continue;
                                Pin<?> pin = action.getPinById(entry.getValue());
                                if (pin == null) continue;
                                pin.removeLink(pinData);
                            }
                            map.clear();
                        }
                    }
                    dragX = x;
                    dragY = y;
                    break;
                }
            }
            if (dragState == DRAG_NONE) {
                dragState = DRAG_SELF;
                dragX = x;
                dragY = y;
            }
        } else if (actionMasked == MotionEvent.ACTION_UP) {
            if (dragState == DRAG_PIN) {
                for (BaseCard<? extends BaseAction> card : cardMap.values()) {
                    int[] location = new int[2];
                    card.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + card.getWidth(), location[1] + card.getHeight()).contains((int) rawX, (int) rawY)) {
                        BasePin<?> basePin = card.getPinByPosition(rawX, rawY);
                        if (basePin != null) {
                            boolean flag = true;
                            // 先判断一下所有插槽的类型是否匹配
                            for (Map.Entry<String, String> entry : links.entrySet()) {
                                BaseAction action = task.getActionById(entry.getKey());
                                if (action == null) continue;
                                Pin<?> pin = action.getPinById(entry.getValue());
                                if (basePin.getPin().getSubType() != pin.getSubType()) {
                                    flag = false;
                                    break;
                                }
                                if (basePin.getPin().getDirection() == pin.getDirection()) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                for (Map.Entry<String, String> entry : links.entrySet()) {
                                    BaseAction action = task.getActionById(entry.getKey());
                                    if (action == null) continue;
                                    Pin<?> pin = action.getPinById(entry.getValue());
                                    // 同一个动作不能连自己
                                    if (!pin.getActionId().equals(basePin.getPin().getActionId())) {
                                        pin.addLink(task, basePin.getPin());
                                        basePin.getPin().addLink(task, pin);
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            dragState = DRAG_NONE;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (dragState == DRAG_CARD) {
                BaseAction action = dragCard.getAction();
                int dx = (int) ((x - dragX) / gridSize);
                if (dx != 0) {
                    action.x += dx;
                    dragX = x;
                }
                int dy = (int) ((y - dragY) / gridSize);
                if (dy != 0) {
                    action.y += dy;
                    dragY = y;
                }
                setCardPosition(dragCard);
            } else if (dragState == DRAG_PIN) {
                dragX = x;
                dragY = y;
                int width = getWidth();
                int height = getHeight();
                int offset = gridSize / 4;
                int areaSize = gridSize * 2;
                if (x < areaSize) {
                    offsetX += offset;
                } else if (x > width - areaSize) {
                    offsetX -= offset;
                }
                if (y < areaSize) {
                    offsetY += offset;
                } else if (y > height - areaSize) {
                    offsetY -= offset;
                }
                setCardsPosition();
            } else if (dragState == DRAG_SELF) {
                offsetX += (x - dragX);
                offsetY += (y - dragY);
                dragX = x;
                dragY = y;
                setCardsPosition();
            }
        }
        postInvalidate();
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLocationOnScreen(location);
    }
}
