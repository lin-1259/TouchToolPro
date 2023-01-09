package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.databinding.PinWidgetImagePickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetImagePicker extends BindingView<PinWidgetImagePickerBinding> {
    private final PinImage pinImage;

    public PinWidgetImagePicker(@NonNull Context context, PinImage pinImage) {
        this(context, null, pinImage);
    }

    public PinWidgetImagePicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinImage());
    }

    public PinWidgetImagePicker(@NonNull Context context, @Nullable AttributeSet attrs, PinImage pinImage) {
        super(context, attrs, PinWidgetImagePickerBinding.class);
        if (pinImage == null) throw new RuntimeException("不是有效的引用");
        this.pinImage = pinImage;

        binding.pickButton.setOnClickListener(v -> {

        });
    }
}
