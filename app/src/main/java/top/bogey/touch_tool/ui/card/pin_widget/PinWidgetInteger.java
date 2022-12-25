package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.action.pin.object.PinInteger;
import top.bogey.touch_tool.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetInteger extends BindingView<PinWidgetInputBinding> {
    private final PinInteger pinInteger;

    public PinWidgetInteger(@NonNull Context context, PinInteger pinInteger) {
        this(context, null, pinInteger);
    }

    public PinWidgetInteger(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinInteger(0));
    }

    public PinWidgetInteger(@NonNull Context context, @Nullable AttributeSet attrs, PinInteger pinInteger) {
        super(context, attrs, PinWidgetInputBinding.class);
        if (pinInteger == null) throw new RuntimeException("不是有效的引用");
        this.pinInteger = pinInteger;

        binding.editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    pinInteger.setValue(0);
                    return;
                }
                pinInteger.setValue(Integer.parseInt(s.toString()));
            }
        });
        binding.editText.setText(String.valueOf(pinInteger.getValue()));
    }
}
