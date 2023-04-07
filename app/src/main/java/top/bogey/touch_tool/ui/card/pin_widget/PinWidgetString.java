package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetString extends BindingView<PinWidgetInputBinding> {

    public PinWidgetString(@NonNull Context context, PinString pinString) {
        this(context, null, pinString);
    }

    public PinWidgetString(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinString());
    }

    public PinWidgetString(@NonNull Context context, @Nullable AttributeSet attrs, PinString pinString) {
        super(context, attrs, PinWidgetInputBinding.class);
        if (pinString == null) throw new RuntimeException("不是有效的引用");

        binding.editText.setSaveEnabled(false);
        binding.editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        binding.editText.setText(pinString.getValue());
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) pinString.setValue(null);
                else pinString.setValue(s.toString());
            }
        });
    }
}
