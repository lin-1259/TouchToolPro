package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.databinding.FloatPickerNodePreviewBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class NodePickerFloatPreview extends BasePickerFloatView {

    public NodePickerFloatPreview(@NonNull Context context, PickerCallback callback, PinString pinString) {
        super(context, callback);

        PinString pinNode = (PinString) pinString.copy();

        FloatPickerNodePreviewBinding binding = FloatPickerNodePreviewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.idTitle.setText(pinNode.getValue());

        binding.pickerButton.setOnClickListener(v -> new NodePickerFloatView(context, () -> binding.idTitle.setText(pinNode.getValue()), pinNode).show());

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinString.setValue(pinNode.getValue());
                callback.onComplete();
            }
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                ArrayList<AccessibilityNodeInfo> roots = service.getNeedWindowsRoot();

                if (pinNode instanceof PinNodePath pinNodePath) {
                    AccessibilityNodeInfo node = pinNodePath.getNode(roots, null);
                    node = getClickAbleParent(node);
                    if (node != null) node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                } else {
                    String nodeId = pinNode.getValue();
                    if (nodeId == null || nodeId.isEmpty()) return;
                    for (AccessibilityNodeInfo root : roots) {
                        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(nodeId);
                        if (nodes.size() == 1) {
                            AccessibilityNodeInfo node = getClickAbleParent(nodes.get(0));
                            if (node != null) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private AccessibilityNodeInfo getClickAbleParent(AccessibilityNodeInfo node) {
        if (node == null) return null;
        if (node.isVisibleToUser()) {
            if (node.isClickable() || node.isEditable() || node.isCheckable() || node.isLongClickable()) return node;
        }
        return getClickAbleParent(node.getParent());
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(true)
                .setCallback(floatCallback)
                .setAnimator(null)
                .show();
    }
}
