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
public class PinInView<P extends PinObject, A extends BaseAction> extends PinBaseView<PinInBinding, P, A> {
    public PinInView(@NonNull Context context, BaseCard<A> card, Pin<P> pin) {
        super(context, PinInBinding.class, card, pin);
    }

    @Override
    public int[] getSlotLocationOnScreen(float scale) {
        int[] location = new int[2];
        pinSlot.getLocationOnScreen(location);
        location[1] += (pinSlot.getHeight() * scale / 2);
        return location;
    }
}
