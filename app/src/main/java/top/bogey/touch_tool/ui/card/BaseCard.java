package top.bogey.touch_tool.ui.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.pin.Pin;
import top.bogey.touch_tool.data.action.pin.PinDirection;
import top.bogey.touch_tool.databinding.CardBaseBinding;
import top.bogey.touch_tool.ui.card.pin.InPin;
import top.bogey.touch_tool.ui.card.pin.OutPin;
import top.bogey.touch_tool.utils.DisplayUtils;

@SuppressLint("ViewConstructor")
public class BaseCard<A extends BaseAction> extends MaterialCardView {
    private final CardBaseBinding binding;
    private final Task task;
    private final A action;

    private boolean needDelete = false;

    public BaseCard(@NonNull Context context, Task task, A action) {
        super(context, null);
        if (action == null) throw new RuntimeException("无效的动作");
        this.task = task;
        this.action = action;

        setCardElevation(DisplayUtils.dp2px(context, 5));
        setStrokeWidth(0);
        setCardBackgroundColor(DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0));
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);

        binding = CardBaseBinding.inflate(LayoutInflater.from(context), this, true);
        binding.copyButton.setOnClickListener(v -> {

        });

        binding.removeButton.setOnClickListener(v -> {
            if (needDelete) {
                binding.removeButton.setChecked(false);
                needDelete = false;

            } else {
                binding.removeButton.setChecked(true);
                needDelete = true;
                postDelayed(() -> {
                    binding.removeButton.setChecked(false);
                    needDelete = false;
                }, 1500);
            }
        });

        binding.title.addOnCheckedChangeListener((button, isChecked) -> {

        });
        binding.title.setChecked(action.isEnable());
        binding.title.setText(action.getTitle(context));

        for (Pin<?> pin : action.getPins()) {
            if (pin.getDirection() == PinDirection.IN) {
                binding.inBox.addView(new InPin(context, action, pin));
            } else if (pin.getDirection() == PinDirection.OUT) {
                binding.outBox.addView(new OutPin(context, action, pin));
            }
        }
    }

    public A getAction() {
        return action;
    }
}
