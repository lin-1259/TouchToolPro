package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.databinding.PinTopCustomBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.utils.DisplayUtils;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinTopCustomView extends PinCustomView {
    private final PinTopCustomBinding binding;

    public PinTopCustomView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin);
        binding = PinTopCustomBinding.inflate(LayoutInflater.from(context), this, true);

        initRemoveButton(binding.removeButton);
        refreshPinUI();

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(functionPin.getTitle())) return;
                functionPin.setTitle(s.toString());
            }
        });
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());

        boolean empty = pin.getLinks().isEmpty();
        binding.pinSlot.setCardBackgroundColor(empty ? DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0) : getPinColor());

        if (!binding.editText.hasFocus() && functionPin.getTitle() != null) {
            binding.editText.setText(functionPin.getTitle());
        }
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        binding.pinSlot.getLocationOnScreen(location);
        location[0] += (binding.pinSlot.getWidth() * scale / 2);
        return location;
    }

    @Override
    public ViewGroup getPinViewBox() {
        return null;
    }
}
