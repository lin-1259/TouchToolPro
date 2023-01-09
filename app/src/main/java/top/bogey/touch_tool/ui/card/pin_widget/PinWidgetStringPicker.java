package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.PinWidgetStringPickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetStringPicker extends BindingView<PinWidgetStringPickerBinding> {
    private final PinString pinString;

    public PinWidgetStringPicker(@NonNull Context context, PinString pinString, PinSubType pinSubType) {
        this(context, null, pinString, pinSubType);
    }

    public PinWidgetStringPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinString(), PinSubType.ID);
    }

    public PinWidgetStringPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinString pinString, PinSubType pinSubType) {
        super(context, attrs, PinWidgetStringPickerBinding.class);
        if (pinString == null) throw new RuntimeException("不是有效的引用");
        this.pinString = pinString;

        binding.title.setText(pinString.getValue());
        if (pinSubType == PinSubType.ID) {
            binding.pickButton.setIconResource(R.drawable.icon_key);
            binding.pickButton.setOnClickListener(v -> {

            });
        } else if (pinSubType == PinSubType.LEVEL) {
            binding.pickButton.setIconResource(R.drawable.icon_layouts);
            binding.pickButton.setOnClickListener(v -> {

            });
        }
    }
}
