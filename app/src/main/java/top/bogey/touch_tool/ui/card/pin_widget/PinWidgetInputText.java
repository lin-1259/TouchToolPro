package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicReference;

import top.bogey.touch_tool.databinding.PinWidgetInputBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetInputText extends BindingView<PinWidgetInputBinding> {
    private final AtomicReference<CharSequence> atomicReference;

    public PinWidgetInputText(@NonNull Context context, AtomicReference<CharSequence> atomicReference) {
        this(context, null, atomicReference);
    }

    public PinWidgetInputText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new AtomicReference<>());
    }

    public PinWidgetInputText(@NonNull Context context, @Nullable AttributeSet attrs, AtomicReference<CharSequence> atomicReference) {
        super(context, attrs, PinWidgetInputBinding.class);
        if (atomicReference == null) throw new RuntimeException("不是有效的引用");
        this.atomicReference = atomicReference;

        binding.editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                atomicReference.set(s);
            }
        });
        binding.editText.setText(atomicReference.get());
    }
}
