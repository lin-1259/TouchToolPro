package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import top.bogey.touch_tool.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetInputInteger extends BindingView<PinWidgetInputBinding> {
    private final AtomicInteger atomicInteger;

    public PinWidgetInputInteger(@NonNull Context context, AtomicInteger atomicInteger) {
        this(context, null, atomicInteger);
    }

    public PinWidgetInputInteger(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new AtomicInteger(0));
    }

    public PinWidgetInputInteger(@NonNull Context context, @Nullable AttributeSet attrs, AtomicInteger atomicInteger) {
        super(context, attrs, PinWidgetInputBinding.class);
        if (atomicInteger == null) throw new RuntimeException("不是有效的引用");
        this.atomicInteger = atomicInteger;

        binding.editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    atomicInteger.set(0);
                    return;
                }
                atomicInteger.set(Integer.parseInt(s.toString()));
            }
        });
        binding.editText.setText(String.valueOf(atomicInteger.get()));
    }
}
