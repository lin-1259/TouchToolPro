package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.databinding.PinWidgetImagePickerBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinWidgetColorPicker extends BindingView<PinWidgetImagePickerBinding> {
    private final PinColor pinColor;

    public PinWidgetColorPicker(@NonNull Context context, PinColor pinColor) {
        this(context, null, pinColor);
    }

    public PinWidgetColorPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinColor());
    }

    public PinWidgetColorPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinColor pinColor) {
        super(context, attrs, PinWidgetImagePickerBinding.class);
        if (pinColor == null) throw new RuntimeException("不是有效的引用");
        this.pinColor = pinColor;

        binding.pickButton.setIconResource(R.drawable.icon_action_color);
        int cornerSize = DisplayUtils.dp2px(context, 12);
        binding.pickButton.setShapeAppearanceModel(ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                .build());

        binding.pickButton.setOnClickListener(v -> {

        });
    }
}
