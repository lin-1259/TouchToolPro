package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.databinding.PinWidgetTouchBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.PickerCallback;
import top.bogey.touch_tool_pro.ui.picker.TouchPickerFloatPreview;

@SuppressLint("ViewConstructor")
public class PinWidgetTouch extends PinWidget<PinTouch> {
    private final PinWidgetTouchBinding binding;

    public PinWidgetTouch(@NonNull Context context, ActionCard<?> card, PinView pinView, PinTouch pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetTouchBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.pathView.setPaths(pinObject.getPaths(getContext()));
        binding.pickButton.setOnClickListener(v -> new TouchPickerFloatPreview(context, new PickerCallback() {
            @Override
            public void onComplete() {
                binding.pathView.setPaths(pinObject.getPaths(getContext()));
            }
        }, pinObject).show());
    }

    @Override
    public void initCustom() {

    }
}
