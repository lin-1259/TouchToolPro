package top.bogey.touch_tool.ui.card.custom;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.databinding.ViewCardCustomBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

@SuppressLint("ViewConstructor")
public class CustomCard extends BindingView<ViewCardCustomBinding> {

    public CustomCard(@NonNull Context context, BaseFunction action) {
        super(context, ViewCardCustomBinding.class);

        binding.addInButton.setOnClickListener(v -> binding.inBox.addView(new CustomCardInItem(context, binding.inBox, null)));
        binding.addOutButton.setOnClickListener(v -> binding.outBox.addView(new CustomCardOutItem(context, binding.outBox, null)));

        if (action != null) {
            binding.editText.setText(action.getTitle());
            for (Pin pin : action.getPins()) {
                if (pin.getDirection().isOut()) {
                    binding.outBox.addView(new CustomCardOutItem(context, binding.outBox, pin));
                } else {
                    binding.inBox.addView(new CustomCardInItem(context, binding.inBox, pin));
                }
            }
        }
    }
}
