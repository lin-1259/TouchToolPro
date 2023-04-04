package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.databinding.PinOutBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

@SuppressLint("ViewConstructor")
public class PinOutView extends PinBaseView<PinOutBinding> {
    public PinOutView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin pin) {
        super(context, PinOutBinding.class, card, pin);

        initRemoveButton(binding.removeButton);
        setValueView();
        refreshPinUI();
    }

    @Override
    public void refreshPinUI() {
        binding.titleBox.setStrokeColor(getPinColor());
        binding.title.setText(pin.getTitle(getContext()));
        binding.pinBox.setVisibility(pin.getLinks().size() > 0 ? GONE : VISIBLE);
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        return null;
    }

    @Override
    public View getSlotBox() {
        return null;
    }

    @Override
    public ViewGroup getPinBox() {
        return binding.pinBox;
    }
}
