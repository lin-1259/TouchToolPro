package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.databinding.PinOutBinding;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinOutView extends PinBaseView<PinOutBinding> {
    public PinOutView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin pin) {
        super(context, PinOutBinding.class, card, pin);

        initRemoveButton(binding.removeButton);
        if (pin.getValue() instanceof PinAdd) {
            setValueView();
        }
        refreshPinUI();
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.title.setText(pin.getTitle(getContext()));

        boolean linked = pin.getLinks().size() > 0;
        binding.pinSlot.setCardBackgroundColor(linked ? getPinColor() : DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0));
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        binding.pinSlot.getLocationOnScreen(location);
        location[0] += (binding.pinSlot.getWidth() * scale);
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
