package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.databinding.PinInBinding;

@SuppressLint("ViewConstructor")
public class InPin extends BasePin<PinInBinding> {
    public InPin(@NonNull Context context, BaseAction action, Pin<?> pin) {
        super(context, PinInBinding.class, action, pin);
    }

    @Override
    public int[] getSlotLocationOnScreen() {
        int[] location = new int[2];
        pinSlot.getLocationOnScreen(location);
        location[0] += (pinSlot.getWidth() / 2);
        location[1] += (pinSlot.getHeight() / 2);
        return location;
    }
}
