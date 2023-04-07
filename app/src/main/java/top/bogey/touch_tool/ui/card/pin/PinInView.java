package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.databinding.PinInBinding;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinInView extends PinBaseView<PinInBinding> {
    public PinInView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin pin) {
        super(context, PinInBinding.class, card, pin);

        initRemoveButton(binding.removeButton);
        setValueView();
        refreshPinUI();
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.title.setText(pin.getTitle(getContext()));
        binding.pinBox.setVisibility(pin.getLinks().size() > 0 ? GONE : VISIBLE);

        boolean linked = pin.getLinks().size() > 0;
        binding.pinSlot.setCardBackgroundColor(linked ? getPinColor() : DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0));
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        binding.pinSlot.getLocationOnScreen(location);
        location[1] += (binding.pinSlot.getHeight() / 2 * scale);
        return location;
    }

    @Override
    public View getSlotBox() {
        return binding.pinSlotBox;
    }

    @Override
    public ViewGroup getPinBox() {
        return binding.pinBox;
    }
}
