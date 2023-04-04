package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.databinding.PinBottomBinding;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinBottomView extends PinBaseView<PinBottomBinding> {
    public PinBottomView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin pin) {
        super(context, PinBottomBinding.class, card, pin);

        initRemoveButton(binding.removeButton);
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
        location[0] += (binding.pinSlot.getWidth() * scale / 2);
        location[1] += (binding.pinSlot.getHeight() * scale);
        return location;
    }

    @Override
    public View getSlotBox() {
        return binding.pinSlotBox;
    }

    @Override
    public ViewGroup getPinBox() {
        return null;
    }

}
