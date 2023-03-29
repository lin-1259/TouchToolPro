package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.databinding.PinBottomBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

@SuppressLint("ViewConstructor")
public class PinBottomView extends PinBaseView<PinBottomBinding> {
    public PinBottomView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin pin) {
        super(context, PinBottomBinding.class, card, pin);

        if (PinAdd.class.equals(pin.getPinClass())) {
            binding.pinBox.setVisibility(VISIBLE);
        } else {
            binding.pinBox.setVisibility(GONE);
        }
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        pinSlot.getLocationOnScreen(location);
        location[0] += (pinSlot.getWidth() * scale / 2);
        location[1] += (pinSlot.getHeight() * scale);
        return location;
    }

    @Override
    public void setValueView() {

    }
}
