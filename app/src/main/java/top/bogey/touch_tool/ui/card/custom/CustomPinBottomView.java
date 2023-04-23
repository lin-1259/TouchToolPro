package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.databinding.PinCustomBottomBinding;
import top.bogey.touch_tool.ui.card.pin.PinBaseView;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class CustomPinBottomView extends PinBaseView<PinCustomBottomBinding> {
    public CustomPinBottomView(@NonNull Context context, CustomCard card, Pin pin) {
        super(context, PinCustomBottomBinding.class, card, pin);
        initRemoveButton(binding.removeButton);
        setValueView();

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    if (s.toString().equals(pin.getTitle(context))) return;
                    ((BaseFunction) card.getActionContext()).setPinTitle(card.getAction(), pin, s.toString());
                }
            }
        });

        refreshPinUI();
    }

    @Override
    public void refreshPinUI() {
        binding.pinSlot.setStrokeColor(getPinColor());
        boolean linked = pin.getLinks().size() > 0;
        binding.pinSlot.setCardBackgroundColor(linked ? getPinColor() : DisplayUtils.getAttrColor(getContext(), com.google.android.material.R.attr.colorSurfaceVariant, 0));
        binding.pinSlot.setShapeAppearanceModel(getPinStyle());

        if (!binding.editText.hasFocus() && pin.getTitle(getContext()) != null) {
            binding.editText.setText(pin.getTitle(getContext()));
        }
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
