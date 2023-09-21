package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.databinding.PinWidgetAddBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;

@SuppressLint("ViewConstructor")
public class PinWidgetAdd extends PinWidget<PinAdd> {
    private final PinWidgetAddBinding binding;

    public PinWidgetAdd(@NonNull Context context, ActionCard<?> card, PinView pinView, PinAdd pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetAddBinding.inflate(LayoutInflater.from(context), this, true);

        init();
    }

    @Override
    public void initBase() {
        binding.addPinButton.setOnClickListener(v -> {
            Pin copy = (Pin) pinObject.getPin().copy();
            copy.newInfo();
            copy.setTitleId(pinObject.getPin().getTitleId());
            copy.setRemoveAble(true);
            card.addPin(copy, pinObject.getOffset());
        });
    }

    @Override
    public void initCustom() {

    }
}
