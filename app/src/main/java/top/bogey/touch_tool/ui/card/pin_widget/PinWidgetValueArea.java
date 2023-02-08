package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinValueArea;
import top.bogey.touch_tool.databinding.PinWidgetValueAreaBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetValueArea extends BindingView<PinWidgetValueAreaBinding> {

    public PinWidgetValueArea(@NonNull Context context, PinValueArea pinValueArea) {
        this(context, null, pinValueArea);
    }

    public PinWidgetValueArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinValueArea(1, 100, 1));
    }

    public PinWidgetValueArea(@NonNull Context context, @Nullable AttributeSet attrs, PinValueArea pinValueArea) {
        super(context, attrs, PinWidgetValueAreaBinding.class);
        if (pinValueArea == null) throw new RuntimeException("不是有效的引用");

        binding.minEdit.setText(String.valueOf(pinValueArea.getCurrMin()));
        binding.maxEdit.setText(String.valueOf(pinValueArea.getCurrMax()));

        binding.lockButton.addOnCheckedChangeListener((button, isChecked) -> {
            button.setIconResource(isChecked ? R.drawable.icon_lock : R.drawable.icon_unlock);
            if (isChecked) binding.maxEdit.setText(binding.maxEdit.getText());
            binding.maxLayout.setEnabled(!isChecked);
            binding.maxEdit.setText(binding.minEdit.getText());
        });
        binding.lockButton.setChecked(pinValueArea.getCurrMin() == pinValueArea.getCurrMax());

        binding.minEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0)
                    pinValueArea.setCurrMin(Integer.parseInt(s.toString()));
                else pinValueArea.setCurrMin(pinValueArea.getValueFrom());

                if (binding.lockButton.isChecked()) {
                    binding.maxEdit.setText(s);
                }
            }
        });

        binding.maxEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0)
                    pinValueArea.setCurrMax(Integer.parseInt(s.toString()));
                else pinValueArea.setCurrMax(pinValueArea.getValueTo());
            }
        });
    }
}
