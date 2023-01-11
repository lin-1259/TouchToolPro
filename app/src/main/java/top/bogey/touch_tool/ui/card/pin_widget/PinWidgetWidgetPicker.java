package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.databinding.PinWidgetWidgetPickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.ui.picker.WidgetPickerFloatPreview;

public class PinWidgetWidgetPicker extends BindingView<PinWidgetWidgetPickerBinding> {

    public PinWidgetWidgetPicker(@NonNull Context context, PinWidget pinWidget) {
        this(context, null, pinWidget);
    }

    public PinWidgetWidgetPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinWidget());
    }

    public PinWidgetWidgetPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinWidget pinWidget) {
        super(context, attrs, PinWidgetWidgetPickerBinding.class);
        if (pinWidget == null) throw new RuntimeException("不是有效的引用");

        binding.idTitle.setText(pinWidget.getId());
        binding.levelTitle.setText(pinWidget.getLevel());
        binding.pickButton.setOnClickListener(v -> new WidgetPickerFloatPreview(context, () -> {
            binding.idTitle.setText(pinWidget.getId());
            binding.levelTitle.setText(pinWidget.getLevel());
        }, pinWidget).show());
    }
}
