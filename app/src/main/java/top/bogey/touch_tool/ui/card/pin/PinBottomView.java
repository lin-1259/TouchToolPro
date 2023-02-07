package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinBottomBinding;
import top.bogey.touch_tool.ui.card.BaseCard;

@SuppressLint("ViewConstructor")
public class PinBottomView extends PinBaseView<PinBottomBinding> {
    public PinBottomView(@NonNull Context context, BaseCard<? extends BaseAction> card, Pin<? extends PinObject> pin) {
        super(context, PinBottomBinding.class, card, pin);

        if (PinAdd.class.equals(pin.getPinClass())) {
            binding.pinBox.setVisibility(VISIBLE);
        } else {
            binding.pinBox.setVisibility(GONE);
        }
    }

    @Override
    public int[] getSlotLocationOnScreen() {
        int[] location = new int[2];
        pinSlot.getLocationOnScreen(location);
        location[0] += (pinSlot.getWidth() / 2);
        location[1] += pinSlot.getHeight();
        return location;
    }
}
