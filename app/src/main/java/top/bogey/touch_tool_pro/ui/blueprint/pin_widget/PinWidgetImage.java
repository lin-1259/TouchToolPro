package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.databinding.PinWidgetImageBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.ImagePickerFloatPreview;

@SuppressLint("ViewConstructor")
public class PinWidgetImage extends PinWidget<PinImage> {
    private final PinWidgetImageBinding binding;

    public PinWidgetImage(@NonNull Context context, ActionCard<?> card, PinView pinView, PinImage pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetImageBinding.inflate(LayoutInflater.from(context), this, true);

        init();
    }

    @Override
    public void initBase() {
        binding.image.setImageBitmap(pinObject.getImage(context));
        binding.pickButton.setOnClickListener(v -> new ImagePickerFloatPreview(context, () -> binding.image.setImageBitmap(pinObject.getImage(context)), pinObject).show());
    }

    @Override
    public void initCustom() {

    }
}
