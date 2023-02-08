package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinOutBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

@SuppressLint("ViewConstructor")
public class PinOutView<P extends PinObject, A extends BaseAction> extends PinBaseView<PinOutBinding, P, A> {
    public PinOutView(@NonNull Context context, BaseCard<A> card, Pin<P> pin) {
        super(context, PinOutBinding.class, card, pin);

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
        location[0] += (pinSlot.getWidth() * scale);
        location[1] += (pinSlot.getHeight() * scale / 2);
        return location;
    }
}
