package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.databinding.PinLeftBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class PinLeftView extends PinView{
    private final PinLeftBinding binding;

    public PinLeftView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin);

        binding = PinLeftBinding.inflate(LayoutInflater.from(context), this, true);
        initRemoveButton(binding.removeButton);
        refreshPinView();
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());
        binding.title.setText(pin.getTitle());

        boolean empty = pin.getLinks().isEmpty();
        binding.pinBox.setVisibility(empty ? VISIBLE : GONE);
        binding.pinSlot.setCardBackgroundColor(empty ? DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0) : getPinColor());
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        binding.pinSlot.getLocationOnScreen(location);
        location[1] += (binding.pinSlot.getHeight() / 2 * scale);
        return location;
    }

    @Override
    public ViewGroup getPinViewBox() {
        return binding.pinBox;
    }

}
