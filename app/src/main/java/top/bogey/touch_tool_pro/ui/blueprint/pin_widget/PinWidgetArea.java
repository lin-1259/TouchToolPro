package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.databinding.PinWidgetAreaBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.AreaPickerFloatPreview;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetArea extends PinWidget<PinArea> {
    private final PinWidgetAreaBinding binding;

    public PinWidgetArea(@NonNull Context context, ActionCard<?> card, PinView pinView, PinArea pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetAreaBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        Rect area = pinObject.getArea(context);
        binding.leftEdit.setText(String.valueOf(area.left));
        binding.topEdit.setText(String.valueOf(area.top));
        binding.rightEdit.setText(String.valueOf(area.right));
        binding.bottomEdit.setText(String.valueOf(area.bottom));

        binding.leftEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int value = 0;
                if (s != null && s.length() > 0) {
                    try {
                        value = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {
                    }
                }
                Rect rect = pinObject.getArea(context);
                rect.left = value;
                pinObject.setArea(context, rect);
            }
        });

        binding.topEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int value = 0;
                if (s != null && s.length() > 0) {
                    try {
                        value = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {
                    }
                }
                Rect rect = pinObject.getArea(context);
                rect.top = value;
                pinObject.setArea(context, rect);
            }
        });

        binding.rightEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int value = 0;
                if (s != null && s.length() > 0) {
                    try {
                        value = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {
                    }
                }
                Rect rect = pinObject.getArea(context);
                rect.right = value;
                pinObject.setArea(context, rect);
            }
        });

        binding.bottomEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int value = 0;
                if (s != null && s.length() > 0) {
                    try {
                        value = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {
                    }
                }
                Rect rect = pinObject.getArea(context);
                rect.bottom = value;
                pinObject.setArea(context, rect);
            }
        });

        binding.pickButton.setOnClickListener(v -> new AreaPickerFloatPreview(context, () -> {
            Rect rect = pinObject.getArea(context);
            binding.leftEdit.setText(String.valueOf(rect.left));
            binding.topEdit.setText(String.valueOf(rect.top));
            binding.rightEdit.setText(String.valueOf(rect.right));
            binding.bottomEdit.setText(String.valueOf(rect.bottom));
        }, pinObject).show());
    }

    @Override
    public void initCustom() {

    }
}
