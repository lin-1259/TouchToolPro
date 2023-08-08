package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.databinding.PinWidgetBooleanBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;

@SuppressLint("ViewConstructor")
public class PinWidgetBoolean extends PinWidget<PinBoolean> {
    private final PinWidgetBooleanBinding binding;

    public PinWidgetBoolean(@NonNull Context context, ActionCard<?> card, PinView pinView, PinBoolean pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetBooleanBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.enableSwitch.setChecked(pinObject.isBool());
        binding.enableSwitch.setOnClickListener(v -> {
            pinObject.setBool(!pinObject.isBool());
            binding.enableSwitch.setChecked(pinObject.isBool());
        });
    }

    @Override
    public void initCustom() {

    }
}
