package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.databinding.PinWidgetCheckboxBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetBoolean extends BindingView<PinWidgetCheckboxBinding> {

    public PinWidgetBoolean(@NonNull Context context, PinBoolean pinBoolean) {
        this(context, null, pinBoolean);
    }

    public PinWidgetBoolean(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinBoolean(false));
    }

    public PinWidgetBoolean(@NonNull Context context, @Nullable AttributeSet attrs, PinBoolean pinBoolean) {
        super(context, attrs, PinWidgetCheckboxBinding.class);
        if (pinBoolean == null) throw new RuntimeException("不是有效的引用");

        binding.enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> pinBoolean.setValue(isChecked));
        binding.enableSwitch.setChecked(pinBoolean.getValue());
    }
}
