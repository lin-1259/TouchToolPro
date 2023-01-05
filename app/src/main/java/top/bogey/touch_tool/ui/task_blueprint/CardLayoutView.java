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
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinDirection;
import top.bogey.touch_tool.data.action.pin.PinSlotType;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
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
    private final Map<String, String> dragLinks = new HashMap<>();
    private BaseCard<? extends BaseAction> dragCard = null;
    private float dragX = 0;
    private float dragY = 0;
    private PinDirection dragDirection;

    private float offsetX = 0;
    private float offsetY = 0;

    public CardLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gridSize = DisplayUtils.dp2px(context, 8);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(5);
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

    public void removeAction(BaseAction action) {
        task.removeAction(action);
        BaseCard<? extends BaseAction> card = cardMap.remove(action.getId());
        if (card == null) return;
        for (Pin<? extends PinObject> pin : action.getPins()) {
            linksRemovePin(pin.getLinks(), card.getPinById(pin.getId()));
        }

        removeView(card);
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
                    PinBaseView<?> pinBaseView = baseCard.getPinById(entry.getValue());
                    if (pinBaseView == null) continue;
                    // 只画输出的线
                    if (pinBaseView.getPin().getDirection() == PinDirection.OUT) {
                        linePaint.setColor(pin.getValue().getPinColor(getContext()));
                        canvas.drawPath(calculateLinePath(pinBaseView, card.getPinById(pin.getId())), linePaint);
                    }
                }
            }
        }
        if (dragState == DRAG_PIN) {
            for (Map.Entry<String, String> entry : dragLinks.entrySet()) {
                BaseCard<? extends BaseAction> card = cardMap.get(entry.getKey());
                if (card == null) continue;
                PinBaseView<?> pinBaseView = card.getPinById(entry.getValue());
                if (pinBaseView == null) continue;
                linePaint.setColor(pinBaseView.getPin().getValue().getPinColor(getContext()));
                canvas.drawPath(calculateLinePath(pinBaseView), linePaint);
            }
        }

        // 所有卡片
        super.dispatchDraw(canvas);
    }

    private Path calculateLinePath(PinBaseView<?> outPin, PinBaseView<?> inPin) {
        Path path = new Path();
        if (outPin == null || inPin == null) return path;
        int[] outLocation = outPin.getSlotLocationOnScreen();
        int[] inLocation = inPin.getSlotLocationOnScreen();

        int offset = inLocation[0] - outLocation[0];
        offset = offset > gridSize * 4 ? offset : gridSize * 8;
        int x1 = outLocation[0] + offset;
        int x2 = inLocation[0] - offset;
        path.moveTo(outLocation[0], outLocation[1]);
        path.cubicTo(x1, outLocation[1], x2, inLocation[1], inLocation[0], inLocation[1]);
        path.offset(-location[0] - getX(), -location[1] - getY());
        return path;
    }

    private Path calculateLinePath(PinBaseView<?> pinBaseView) {
        Path path = new Path();
        if (pinBaseView == null) return path;
        int[] pinLocation = pinBaseView.getSlotLocationOnScreen();

        int[] outLocation, inLocation;
        if (dragDirection == PinDirection.OUT) {
            inLocation = pinLocation;
            outLocation = new int[] {(int) dragX, (int) dragY};
        } else {
            inLocation = new int[] {(int) dragX, (int) dragY};
            outLocation = pinLocation;
        }
        int offset = inLocation[0] - outLocation[0];
        offset = offset > gridSize * 4 ? offset : gridSize * 8;
        int x1 = outLocation[0] + offset;
        int x2 = inLocation[0] - offset;
        path.moveTo(outLocation[0], outLocation[1]);
        path.cubicTo(x1, outLocation[1], x2, inLocation[1], inLocation[0], inLocation[1]);
        path.offset(-location[0] - getX(), -location[1] - getY());
        return path;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        int actionMasked = event.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            dragLinks.clear();
            ArrayList<BaseCard<? extends BaseAction>> baseCards = new ArrayList<>(cardMap.values());
            for (int i = baseCards.size() - 1; i >= 0; i--) {
                BaseCard<? extends BaseAction> card = baseCards.get(i);
                int[] location = new int[2];
                card.getLocationOnScreen(location);
                if (new Rect(location[0], location[1], location[0] + card.getWidth(), location[1] + card.getHeight()).contains((int) rawX, (int) rawY)) {
                    dragState = DRAG_CARD;
                    dragCard = card;
                    PinBaseView<?> pinBaseView = card.getPinByPosition(rawX, rawY);
                    if (pinBaseView != null) {
                        dragState = DRAG_PIN;
                        Pin<?> pin = pinBaseView.getPin();
                        Map<String, String> links = pin.getLinks();
                        // 数量为0 或者 是出线且可以出多条线，从这个点出线。进线要么连接，要么断开
                        if (links.size() == 0 || (pin.getSlotType() == PinSlotType.MULTI && pin.getDirection() == PinDirection.OUT)) {
                            dragLinks.put(pin.getActionId(), pin.getId());
                            // 目标方向与自身相反
                            dragDirection = pin.getDirection() == PinDirection.IN ? PinDirection.OUT : PinDirection.IN;
                        } else {
                            // 否则就是挪线
                            dragLinks.putAll(links);
                            dragDirection = pin.getDirection();
                            linksRemovePin(links, pinBaseView);
                            links.clear();
                            pinBaseView.refreshPinUI();
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
                for (BaseCard<? extends BaseAction> baseCard : cardMap.values()) {
                    int[] location = new int[2];
                    baseCard.getLocationOnScreen(location);
                    if (new Rect(location[0], location[1], location[0] + baseCard.getWidth(), location[1] + baseCard.getHeight()).contains((int) rawX, (int) rawY)) {

                        PinBaseView<?> pinBaseView = baseCard.getPinByPosition(rawX, rawY);
                        if (pinBaseView == null) continue;
                        if (pinAddLinks(pinBaseView, dragLinks)) break;
                    }
                }
            }
            dragLinks.clear();
            dragCard = null;
            dragState = DRAG_NONE;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (dragState == DRAG_CARD) {
                BaseAction action = dragCard.getAction();
                int dx = (int) ((rawX - dragX) / gridSize);
                if (dx != 0) {
                    action.x += dx;
                    dragX = rawX;
                }
                int dy = (int) ((rawY - dragY) / gridSize);
                if (dy != 0) {
                    action.y += dy;
                    dragY = rawY;
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

    private boolean pinAddLinks(PinBaseView<?> pinBaseView, Map<String, String> links) {
        Pin<?> pin = pinBaseView.getPin();
        boolean flag = true;
        // 先判断一下插槽是否匹配
        for (Map.Entry<String, String> entry : dragLinks.entrySet()) {
            BaseAction action = task.getActionById(entry.getKey());
            if (action == null) continue;
            Pin<?> linkPin = action.getPinById(entry.getValue());
            if (!pin.getValue().getClass().equals(linkPin.getValue().getClass())) {
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
                BaseCard<? extends BaseAction> card = cardMap.get(entry.getKey());
                if (card == null) continue;
                PinBaseView<?> cardPin = card.getPinById(entry.getValue());
                if (cardPin == null) continue;
                // 不能自己首尾相连
                if (pin.getActionId().equals(cardPin.getPin().getActionId())) continue;

                linksRemovePin(cardPin.addLink(pin), cardPin);
                linksRemovePin(pinBaseView.addLink(cardPin.getPin()), pinBaseView);
            }
        }
        return flag;
    }

    private void linksRemovePin(Map<String, String> links, PinBaseView<?> pinBaseView) {
        for (Map.Entry<String, String> entry : links.entrySet()) {
            BaseCard<? extends BaseAction> card = cardMap.get(entry.getKey());
            if (card == null) continue;
            PinBaseView<?> cardPin = card.getPinById(entry.getValue());
            if (cardPin == null) continue;
            cardPin.removeLink(pinBaseView.getPin());
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getLocationOnScreen(location);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        TaskRepository.getInstance().saveTask(task);
    }
}
