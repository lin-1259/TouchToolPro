package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import top.bogey.touch_tool.data.pin.object.PinValueArea;
import top.bogey.touch_tool.databinding.PinWidgetValueAreaBinding;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetValueArea extends BindingView<PinWidgetValueAreaBinding> {
    private final PinValueArea pinValueArea;

    public PinWidgetValueArea(@NonNull Context context, PinValueArea pinValueArea) {
        this(context, null, pinValueArea);
    }

    public PinWidgetValueArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinValueArea(1, 100, 1));
    }

    public PinWidgetValueArea(@NonNull Context context, @Nullable AttributeSet attrs, PinValueArea pinValueArea) {
        super(context, attrs, PinWidgetValueAreaBinding.class);
        if (pinValueArea == null) throw new RuntimeException("不是有效的引用");
        this.pinValueArea = pinValueArea;

        binding.rangeSlider.setValueFrom(pinValueArea.getValueFrom());
        binding.rangeSlider.setValueTo(pinValueArea.getValueTo());
        binding.rangeSlider.setStepSize(pinValueArea.getStep());
        binding.rangeSlider.setValues((float) pinValueArea.getCurrMin(), (float) pinValueArea.getCurrMax());
        binding.rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            pinValueArea.setCurrMin(Math.round(values.get(0)));
            pinValueArea.setCurrMax(Math.round(values.get(values.size() - 1)));
        });
    }
}
