package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinInBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

@SuppressLint("ViewConstructor")
public class PinInView extends PinBaseView<PinInBinding> {
    public PinInView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin<? extends PinObject> pin) {
        super(context, PinInBinding.class, card, pin);
    }

    @Override
    public int[] getSlotLocationOnScreen() {
        int[] location = new int[2];
        pinSlot.getLocationOnScreen(location);
        location[1] += (pinSlot.getHeight() / 2);
        return location;
    }
}
