package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinArea;
import top.bogey.touch_tool.databinding.PinWidgetAreaBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.ui.picker.AreaPickerFloatPreview;
import top.bogey.touch_tool.ui.picker.AreaPickerFloatView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetArea extends BindingView<PinWidgetAreaBinding> {

    public PinWidgetArea(@NonNull Context context, PinArea pinArea) {
        this(context, null, pinArea);
    }

    public PinWidgetArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinArea());
    }

    public PinWidgetArea(@NonNull Context context, @Nullable AttributeSet attrs, PinArea pinArea) {
        super(context, attrs, PinWidgetAreaBinding.class);
        if (pinArea == null) throw new RuntimeException("不是有效的引用");

        Rect area = pinArea.getArea(context);
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
                Rect rect = pinArea.getArea(context);
                rect.left = value;
                pinArea.setArea(context, rect);
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
                Rect rect = pinArea.getArea(context);
                rect.top = value;
                pinArea.setArea(context, rect);
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
                Rect rect = pinArea.getArea(context);
                rect.right = value;
                pinArea.setArea(context, rect);
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
                Rect rect = pinArea.getArea(context);
                rect.bottom = value;
                pinArea.setArea(context, rect);
            }
        });

        binding.pickButton.setOnClickListener(v -> new AreaPickerFloatPreview(context, () -> {
            Rect rect = pinArea.getArea(context);
            binding.leftEdit.setText(String.valueOf(rect.left));
            binding.topEdit.setText(String.valueOf(rect.top));
            binding.rightEdit.setText(String.valueOf(rect.right));
            binding.bottomEdit.setText(String.valueOf(rect.bottom));
        }, pinArea).show());
    }
}
