package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.databinding.PinWidgetPathPickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetPathPicker extends BindingView<PinWidgetPathPickerBinding> {
    private final PinPath pinPath;

    public PinWidgetPathPicker(@NonNull Context context, PinPath pinPath) {
        this(context, null, pinPath);
    }

    public PinWidgetPathPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinPath());
    }

    public PinWidgetPathPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinPath pinPath) {
        super(context, attrs, PinWidgetPathPickerBinding.class);
        if (pinPath == null) throw new RuntimeException("不是有效的引用");
        this.pinPath = pinPath;

        binding.pathView.setPaths(pinPath.getPaths(context));
        binding.pickButton.setOnClickListener(v -> {

        });
    }
}
