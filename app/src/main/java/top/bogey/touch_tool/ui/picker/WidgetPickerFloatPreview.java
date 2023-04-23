package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.action.TouchNodeAction;
import top.bogey.touch_tool.data.pin.object.PinValue;
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.data.pin.object.PinXPath;
import top.bogey.touch_tool.databinding.FloatPickerWidgetPreviewBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class WidgetPickerFloatPreview extends BasePickerFloatView {
    private PinWidget pinWidget;
    private PinXPath pinXPath;

    public WidgetPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinValue pinValue) {
        super(context, callback);
        boolean isWidget = pinValue instanceof PinWidget;

        FloatPickerWidgetPreviewBinding binding = FloatPickerWidgetPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        if (isWidget) {
            pinWidget = (PinWidget) pinValue.copy();
            binding.idTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_id, pinWidget.getId()));
            binding.levelTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_level, pinWidget.getLevel()));

            binding.pickerButton.setOnClickListener(v -> new WidgetPickerFloatView(context, () -> {
                binding.idTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_id, pinWidget.getId()));
                binding.levelTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_level, pinWidget.getLevel()));
            }, pinWidget).show());
        } else {
            pinXPath = (PinXPath) pinValue.copy();
            binding.idTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_xpath, pinXPath.getPath()));
            binding.levelTitle.setVisibility(GONE);

            binding.pickerButton.setOnClickListener(v -> new XPathPickerFloatView(context, () -> {
                binding.idTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_xpath, pinXPath.getPath()));
            }, pinXPath).show());
        }

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                if (isWidget) {
                    PinWidget widget = (PinWidget) pinValue;
                    widget.setId(pinWidget.getId());
                    widget.setLevel(pinWidget.getLevel());
                } else {
                    PinXPath xPath = (PinXPath) pinValue;
                    xPath.setPath(pinXPath.getPath());
                }
                callback.onComplete();
            }
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                if (isWidget) {
                    AccessibilityNodeInfo node = pinWidget.getNode(DisplayUtils.getScreenArea(service), service.getRootInActiveWindow(), true);
                    AccessibilityNodeInfo clickAbleParent = TouchNodeAction.getClickAbleParent(node);
                    if (clickAbleParent != null) clickAbleParent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    AccessibilityNodeInfo node = pinXPath.getPathNode(service.getRootInActiveWindow(), null);
                    AccessibilityNodeInfo clickAbleParent = TouchNodeAction.getClickAbleParent(node);
                    if (clickAbleParent != null) clickAbleParent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        });

        binding.playButton.setOnLongClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                if (isWidget) {
                    AccessibilityNodeInfo node = pinWidget.getNode(DisplayUtils.getScreenArea(service), service.getRootInActiveWindow(), true);
                    AccessibilityNodeInfo clickAbleParent = TouchNodeAction.getClickAbleParent(node);
                    if (clickAbleParent != null) clickAbleParent.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                } else {
                    AccessibilityNodeInfo node = pinXPath.getPathNode(service.getRootInActiveWindow(), null);
                    AccessibilityNodeInfo clickAbleParent = TouchNodeAction.getClickAbleParent(node);
                    if (clickAbleParent != null) clickAbleParent.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
                }
            }
            return true;
        });
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
