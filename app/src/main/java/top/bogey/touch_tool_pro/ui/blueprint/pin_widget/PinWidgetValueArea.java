package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.databinding.PinWidgetValueAreaBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetValueArea extends PinWidget<PinValueArea> {
    private final PinWidgetValueAreaBinding binding;
    private boolean locked;

    public PinWidgetValueArea(@NonNull Context context, ActionCard<?> card, PinView pinView, PinValueArea pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetValueAreaBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {

        binding.lowEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int low;
                try {
                    low = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    low = pinObject.getMin();
                }
                if (low == pinObject.getLow()) return;
                pinObject.setLow(low);

                if (binding.lockButton.isChecked()) {
                    binding.highEdit.setText(s);
                }
            }
        });

        binding.highEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int high;
                try {
                    high = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    high = pinObject.getMax();
                }
                if (high == pinObject.getHigh()) return;
                pinObject.setHigh(high);
            }
        });

        binding.lockButton.setOnClickListener(v -> {
            locked = !locked;
            binding.highEdit.setEnabled(!locked);
            binding.highEdit.setText(binding.lowEdit.getText());
            binding.lockButton.setChecked(locked);
            binding.lockButton.setIconResource(locked ? R.drawable.icon_lock : R.drawable.icon_unlock);
        });

        binding.lockButton.addOnCheckedChangeListener((button, isChecked) -> {
            binding.lockButton.setChecked(isChecked);
            binding.highEdit.setEnabled(!isChecked);
        });
        locked = pinObject.getLow() == pinObject.getHigh();
        binding.lockButton.setChecked(locked);
        binding.lockButton.setIconResource(locked ? R.drawable.icon_lock : R.drawable.icon_unlock);
        refreshText();
    }

    @Override
    public void initCustom() {
        binding.moreBox.setVisibility(VISIBLE);
        refreshText();

        binding.step.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int step;
                try {
                    step = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    step = pinObject.getStep();
                }
                if (step == pinObject.getStep()) return;
                pinObject.setArea(pinObject.getMin(), pinObject.getMax(), step);
                refreshText();
            }
        });

        binding.minEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int min;
                try {
                    min = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    min = pinObject.getMin();
                }
                if (min == pinObject.getMin()) return;
                pinObject.setArea(min, pinObject.getMax(), pinObject.getStep());
                refreshText();
            }
        });

        binding.maxEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int max;
                try {
                    max = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    max = pinObject.getMax();
                }
                if (max == pinObject.getMax()) return;
                pinObject.setArea(pinObject.getMin(), max, pinObject.getStep());
                refreshText();
            }
        });
    }

    private void refreshText() {
        binding.lowEdit.setTextKeepState(String.valueOf(pinObject.getLow()));
        binding.highEdit.setTextKeepState(String.valueOf(pinObject.getHigh()));
        binding.step.setTextKeepState(String.valueOf(pinObject.getStep()));
        binding.minEdit.setTextKeepState(String.valueOf(pinObject.getMin()));
        binding.maxEdit.setTextKeepState(String.valueOf(pinObject.getMax()));
    }
}
