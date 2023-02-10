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
        gridPaint.setColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorError, 0));

        binding = FloatPickerWidgetBinding.inflate(LayoutInflater.from(context), this, true);
        MainAccessibilityService service = MainApplication.getService();
        rootNode = service.getRootInActiveWindow();

        binding.saveButton.setOnClickListener(v -> {
            pinWidget.setId(selectId);
            pinWidget.setLevel(selectLevel);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.markBox.setOnClickListener(v -> showWordView(null));

        selectNode = pinWidget.getNode(rootNode);
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
        canvas.drawRect(bounds, gridPaint);

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            drawNode(canvas, nodeInfo.getChild(i));
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
        Map<Integer, AccessibilityNodeInfo> deepNodeInfo = new HashMap<>();
        findNodeIn(deepNodeInfo, 1, rootNode, x, y);
        int max = 0;
        AccessibilityNodeInfo node = null;
        for (Map.Entry<Integer, AccessibilityNodeInfo> entry : deepNodeInfo.entrySet()) {
            if (max == 0 || entry.getKey() > max) {
                max = entry.getKey();
                node = entry.getValue();
            }
        }
        return node;
    }
    /*

    */

    private void findNodeIn(Map<Integer, AccessibilityNodeInfo> deepNodeInfo, int deep, @NonNull AccessibilityNodeInfo nodeInfo, int x, int y) {
        if (nodeInfo.getChildCount() == 0) return;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo child = nodeInfo.getChild(i);
            if (child != null) {
                Rect rect = new Rect();
                child.getBoundsInScreen(rect);
                if (rect.contains(x, y)) {
                    deepNodeInfo.put(deep, child);
                    findNodeIn(deepNodeInfo, deep + 1, child, x, y);
                }
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
