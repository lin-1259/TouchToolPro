package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.databinding.FloatPickerWidgetBinding;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class WidgetPickerFloatView extends BasePickerFloatView {
    protected final FloatPickerWidgetBinding binding;

    private final Paint gridPaint;
    private final int[] location = new int[2];

    private final AccessibilityNodeInfo rootNode;
    private AccessibilityNodeInfo selectNode;
    private String selectId;
    private String selectLevel;

    public WidgetPickerFloatView(@NonNull Context context, PickerCallback callback, PinWidget pinWidget) {
        super(context, callback);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeWidth(2);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStyle(Paint.Style.STROKE);

        binding = FloatPickerWidgetBinding.inflate(LayoutInflater.from(context), this, true);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        rootNode = service.getRootInActiveWindow();

        binding.saveButton.setOnClickListener(v -> {
            pinWidget.setId(selectId);
            pinWidget.setLevel(selectLevel);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.markBox.setOnClickListener(v -> showWordView(null));

        selectNode = pinWidget.getNode(DisplayUtils.getScreenArea(service), rootNode);
        showWordView(selectNode);
    }

    public void showWordView(AccessibilityNodeInfo nodeInfo) {
        selectNode = nodeInfo;
        binding.markBox.setVisibility(INVISIBLE);
        binding.idTitle.setVisibility(INVISIBLE);
        binding.levelTitle.setVisibility(INVISIBLE);
        if (selectNode != null) {
            binding.markBox.setVisibility(VISIBLE);
            binding.idTitle.setVisibility(VISIBLE);
            binding.levelTitle.setVisibility(VISIBLE);
            setNodeSelectInfo();

            binding.idTitle.setText(selectId);
            binding.idTitle.setVisibility(selectId == null ? INVISIBLE : VISIBLE);
            binding.levelTitle.setText(selectLevel);
            binding.levelTitle.setVisibility(selectLevel == null ? INVISIBLE : VISIBLE);

            Rect rect = new Rect();

            selectNode.getBoundsInScreen(rect);
            ViewGroup.LayoutParams params = binding.markBox.getLayoutParams();
            params.width = rect.width();
            params.height = rect.height();
            binding.markBox.setLayoutParams(params);
            binding.markBox.setX(rect.left);
            binding.markBox.setY(rect.top - location[1]);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        long drawingTime = getDrawingTime();

        drawChild(canvas, binding.getRoot(), drawingTime);
        drawNode(canvas, rootNode);

        if (selectNode != null) {
            drawChild(canvas, binding.markBox, drawingTime);
            drawChild(canvas, binding.idTitle, drawingTime);
            drawChild(canvas, binding.levelTitle, drawingTime);
        }

        drawChild(canvas, binding.buttonBox, drawingTime);
    }

    private void drawNode(Canvas canvas, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;

        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);
        bounds.offset(-location[0], -location[1]);

        boolean touchAble = nodeInfo.isClickable() || nodeInfo.isEditable() || nodeInfo.isCheckable() || nodeInfo.isLongClickable();

        if (!touchAble && nodeInfo.isVisibleToUser()) {
            gridPaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimary, 0));
            canvas.drawRect(bounds, gridPaint);
        }

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            drawNode(canvas, nodeInfo.getChild(i));
        }

        if (touchAble && nodeInfo.isVisibleToUser()) {
            gridPaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorPrimaryInverse, 0));
            bounds.offset(2, 2);
            bounds.right -= 4;
            bounds.bottom -= 4;
            canvas.drawRect(bounds, gridPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            AccessibilityNodeInfo node = getNodeIn((int) x, (int) y);
            if (node != null) {
                showWordView(node);
            }
        }
        return true;
    }

    private void setNodeSelectInfo() {
        String resourceName = selectNode.getViewIdResourceName();
        if (resourceName != null && !resourceName.isEmpty()) {
            Pattern pattern = Pattern.compile(".+:(id/.+)");
            Matcher matcher = pattern.matcher(resourceName);
            if (matcher.find() && matcher.group(1) != null) {
                selectId = matcher.group(1);
            }
        } else selectId = null;

        selectLevel = getNodeLevel(selectNode);
    }

    private String getNodeLevel(AccessibilityNodeInfo nodeInfo) {
        AccessibilityNodeInfo parent = nodeInfo.getParent();
        if (parent != null) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                AccessibilityNodeInfo child = parent.getChild(i);
                if (child != null && child.equals(nodeInfo)) {
                    String level = getNodeLevel(parent);
                    if (level != null && !level.isEmpty()) {
                        return level + "," + i;
                    } else {
                        return String.valueOf(i);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private AccessibilityNodeInfo getNodeIn(int x, int y) {
        if (rootNode == null) return null;
        HashMap<AccessibilityNodeInfo, Integer> infoMap = new HashMap<>();
        findNodeIn(infoMap, 0, rootNode, x, y);

        int min = Integer.MAX_VALUE;
        int deep = 0;
        AccessibilityNodeInfo node = null;
        Rect bounds = new Rect();
        for (Map.Entry<AccessibilityNodeInfo, Integer> entry : infoMap.entrySet()) {
            entry.getKey().getBoundsInScreen(bounds);
            int size = bounds.width() * bounds.height();
            // 范围最小或者深度最深
            if (size < min || (size == min && deep < entry.getValue())) {
                min = size;
                deep = entry.getValue();
                node = entry.getKey();
            }
        }
        return node;
    }

    private void findNodeIn(HashMap<AccessibilityNodeInfo, Integer> infoHashSet, int deep, @NonNull AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo.getChildCount() == 0) return;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                Rect rect = new Rect();
                child.getBoundsInScreen(rect);
                if (rect.contains(x, y) && child.isVisibleToUser()) {
                    infoHashSet.put(child, deep);
                }
                findNodeIn(infoHashSet, deep + 1, child, x, y);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            getLocationOnScreen(location);
        }
    }
}
