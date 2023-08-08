package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.databinding.PinWidgetImageBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.ImagePickerFloatPreview;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinWidgetColor extends PinWidget<PinColor> {
    private final PinWidgetImageBinding binding;

    public PinWidgetColor(@NonNull Context context, ActionCard<?> card, PinView pinView, PinColor pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetImageBinding.inflate(LayoutInflater.from(context), this, true);

        init();
    }

    @Override
    public void initBase() {
        binding.pickButton.setIconResource(R.drawable.icon_color);
        int cornerSize = Math.round(DisplayUtils.dp2px(context, 12));
        binding.image.setShapeAppearanceModel(ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                .build());
        binding.image.setBackgroundColor(DisplayUtils.getColorFromHsv(pinObject.getColor()));
        binding.pickButton.setOnClickListener(v -> new ImagePickerFloatPreview(context, () -> binding.image.setBackgroundColor(DisplayUtils.getColorFromHsv(pinObject.getColor())), pinObject).show());
    }

    @Override
    public void initCustom() {

    }
}
