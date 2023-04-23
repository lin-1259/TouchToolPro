package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinXPath;
import top.bogey.touch_tool.databinding.PinWidgetXpathBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.ui.picker.WidgetPickerFloatPreview;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetXPathPicker extends BindingView<PinWidgetXpathBinding> {

    public PinWidgetXPathPicker(@NonNull Context context, PinXPath pinXPath) {
        this(context, null, pinXPath);
    }

    public PinWidgetXPathPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinXPath());
    }

    public PinWidgetXPathPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinXPath pinXPath) {
        super(context, attrs, PinWidgetXpathBinding.class);
        if (pinXPath == null) throw new RuntimeException("不是有效的引用");

        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);
        binding.editText.setText(pinXPath.getPath());
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) pinXPath.setPath((String) null);
                else pinXPath.setPath(s.toString());
            }
        });

        binding.pickButton.setOnClickListener(v -> new WidgetPickerFloatPreview(context, () -> binding.editText.setText(pinXPath.getPath()), pinXPath).show());
    }
}
