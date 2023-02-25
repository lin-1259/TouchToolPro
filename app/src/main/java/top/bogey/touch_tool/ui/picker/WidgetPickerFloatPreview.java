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
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.databinding.FloatPickerWidgetPreviewBinding;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class WidgetPickerFloatPreview extends BasePickerFloatView {
    private final PinWidget newPinWidget;

    public WidgetPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinWidget pinWidget) {
        super(context, callback);
        newPinWidget = new PinWidget(pinWidget.getId(), pinWidget.getLevel());

        FloatPickerWidgetPreviewBinding binding = FloatPickerWidgetPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.idTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_id, pinWidget.getId()));
        binding.levelTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_level, pinWidget.getLevel()));
        binding.pickerButton.setOnClickListener(v -> new WidgetPickerFloatView(context, () -> {
            binding.idTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_id, newPinWidget.getId()));
            binding.levelTitle.setText(context.getString(R.string.picker_widget_preview_subtitle_level, newPinWidget.getLevel()));
        }, newPinWidget).show());

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinWidget.setId(newPinWidget.getId());
                pinWidget.setLevel(newPinWidget.getLevel());
                callback.onComplete();
            }
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                AccessibilityNodeInfo node = newPinWidget.getNode(service.getRootInActiveWindow());
                AccessibilityNodeInfo clickAbleParent = TouchNodeAction.getClickAbleParent(node);
                if (clickAbleParent != null) clickAbleParent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        });

        binding.playButton.setOnLongClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                AccessibilityNodeInfo node = newPinWidget.getNode(service.getRootInActiveWindow());
                AccessibilityNodeInfo clickAbleParent = TouchNodeAction.getClickAbleParent(node);
                if (clickAbleParent != null) clickAbleParent.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
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
