package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amrdeveloper.treeview.TreeNodeManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.databinding.FloatPickerWidgetBinding;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class WidgetPickerFloatView extends BasePickerFloatView implements WidgetPickerTreeAdapter.SelectNode {
    protected final FloatPickerWidgetBinding binding;
    private final WidgetPickerTreeAdapter adapter;

    private final Paint gridPaint;
    private final Paint markPaint;
    private final int[] location = new int[2];

    private final ArrayList<AccessibilityNodeInfo> rootNodes;
    private AccessibilityNodeInfo selectNode;
    private String selectId;
    private String selectLevel;

    public WidgetPickerFloatView(@NonNull Context context, PickerCallback callback, PinWidget pinWidget) {
        super(context, callback);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStyle(Paint.Style.STROKE);

        markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markPaint.setStyle(Paint.Style.FILL);
        markPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        binding = FloatPickerWidgetBinding.inflate(LayoutInflater.from(context), this, true);
        MainAccessibilityService service = MainApplication.getInstance().getService();
        rootNodes = service.getNeedWindowsRoot();

        adapter = new WidgetPickerTreeAdapter(new TreeNodeManager(), this);
        binding.widgetRecyclerView.setAdapter(adapter);
        adapter.setRoots(rootNodes);

        binding.saveButton.setOnClickListener(v -> {
            pinWidget.setId(selectId);
            pinWidget.setLevel(selectLevel);
            if (callback != null) callback.onComplete();
            dismiss();
        });

        binding.detailButton.setOnClickListener(v -> {
            BottomSheetBehavior<FrameLayout> sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.markBox.setOnClickListener(v -> showWidgetView(null));

        selectNode = pinWidget.getNode(DisplayUtils.getScreenArea(service), rootNodes, true);
        showWidgetView(selectNode);
    }

    @Override
    public void selectNode(AccessibilityNodeInfo nodeInfo) {
        selectNode = nodeInfo;

        binding.markBox.setVisibility(INVISIBLE);
        binding.idTitle.setVisibility(INVISIBLE);
        binding.levelTitle.setVisibility(INVISIBLE);
        binding.detailButton.setVisibility(GONE);

        if (selectNode != null) {
            binding.markBox.setVisibility(VISIBLE);
            binding.idTitle.setVisibility(VISIBLE);
            binding.levelTitle.setVisibility(VISIBLE);
            binding.detailButton.setVisibility(VISIBLE);
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
        } else {
            postInvalidate();
        }
    }

    public void showWidgetView(AccessibilityNodeInfo nodeInfo) {
        selectNode(nodeInfo);
        adapter.setSelectedNode(nodeInfo);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        long drawingTime = getDrawingTime();

        drawChild(canvas, binding.getRoot(), drawingTime);

        canvas.saveLayer(getLeft(), getTop(), getRight(), getBottom(), gridPaint);
        for (int i = rootNodes.size() - 1; i >= 0; i--) {
            canvas.save();
            AccessibilityNodeInfo rootNode = rootNodes.get(i);
            Rect bounds = new Rect();
            rootNode.getBoundsInScreen(bounds);
            if (bounds.width() != getWidth() || bounds.height() != getHeight()) {
                bounds.offset(-location[0], -location[1]);
                canvas.drawRect(bounds, markPaint);
            }
            drawNode(canvas, rootNode);
            canvas.restore();
        }
        canvas.restore();

        if (selectNode != null) {
            canvas.save();
            int[] boxLocation = new int[2];
            binding.markBox.getLocationOnScreen(boxLocation);
            boxLocation[0] -= location[0];
            boxLocation[1] -= location[1];
            Rect rect = new Rect(boxLocation[0], boxLocation[1], binding.markBox.getWidth() + boxLocation[0], binding.markBox.getHeight() + boxLocation[1]);
            canvas.drawRect(rect, markPaint);
            canvas.restore();

            drawChild(canvas, binding.markBox, drawingTime);
            drawChild(canvas, binding.idTitle, drawingTime);
            drawChild(canvas, binding.levelTitle, drawingTime);
        }

        drawChild(canvas, binding.buttonBox, drawingTime);
        drawChild(canvas, binding.bottomSheet, drawingTime);
    }

    private void drawNode(Canvas canvas, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;

        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);
        bounds.offset(-location[0], -location[1]);

        boolean touchAble = nodeInfo.isClickable() || nodeInfo.isEditable() || nodeInfo.isCheckable() || nodeInfo.isLongClickable();

        if (!touchAble && nodeInfo.isVisibleToUser()) {
            gridPaint.setColor(DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSecondary, 0));
            gridPaint.setStrokeWidth(1);
            canvas.drawRect(bounds, gridPaint);
        }

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            drawNode(canvas, nodeInfo.getChild(i));
        }

        if (touchAble && nodeInfo.isVisibleToUser()) {
            gridPaint.setColor(DisplayUtils.getAttrColor(getContext(), R.attr.colorPrimaryLight, 0));
            gridPaint.setStrokeWidth(3);
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
                showWidgetView(node);
            }
        }
        return true;
    }

    private void setNodeSelectInfo() {
        selectId = selectNode.getViewIdResourceName();
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
        if (rootNodes == null || rootNodes.size() == 0) return null;
        HashMap<AccessibilityNodeInfo, Integer> infoMap = new HashMap<>();
        for (int i = 0; i < rootNodes.size(); i++) {
            AccessibilityNodeInfo rootNode = rootNodes.get(i);
            findNodeIn(infoMap, (rootNodes.size() - i) * Short.MAX_VALUE, rootNode, x, y);
        }

        int deep = 0;
        AccessibilityNodeInfo node = null;
        for (Map.Entry<AccessibilityNodeInfo, Integer> entry : infoMap.entrySet()) {
            // 深度最深
            if (deep < entry.getValue()) {
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
