package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetInteger extends PinWidget<PinInteger> {
    private final PinWidgetInputBinding binding;

    public PinWidgetInteger(@NonNull Context context, ActionCard<?> card, PinView pinView, PinInteger pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetInputBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);
        binding.editText.setInputType(EditorInfo.TYPE_NUMBER_FLAG_SIGNED);

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    pinObject.setValue(0);
                    return;
                }
                try {
                    pinObject.setValue(Integer.parseInt(s.toString()));
                } catch (NumberFormatException ignored) {
                    pinObject.setValue(pinObject.getValue());
                    binding.editText.setTextKeepState(String.valueOf(pinObject.getValue()));
                }
            }
        });
        binding.editText.setText(String.valueOf(pinObject.getValue()));
    }

    @Override
    public void initCustom() {

    }
}
