package top.bogey.touch_tool.ui.card.pin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinOutBinding;

@SuppressLint("ViewConstructor")
public class OutPin extends BasePin<PinOutBinding> {
    public OutPin(@NonNull Context context, BaseAction action, Pin<? extends PinObject> pin) {
        super(context, PinOutBinding.class, action, pin);
        FrameLayout.LayoutParams params = (LayoutParams) binding.getRoot().getLayoutParams();
        if (params == null) params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        binding.getRoot().setLayoutParams(params);
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
