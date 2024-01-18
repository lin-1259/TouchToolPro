package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.databinding.FloatPickerTextPreviewBinding;
import top.bogey.touch_tool_pro.utils.TextChangedListener;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;


@SuppressLint("ViewConstructor")
public class TextPickerFloatPreview extends BasePickerFloatView {
    private final PinString newPinString;

    @SuppressLint("DefaultLocale")
    public TextPickerFloatPreview(@NonNull Context context, IPickerCallback callback, PinString pinString) {
        super(context, callback);
        newPinString = (PinString) pinString.copy();

        FloatPickerTextPreviewBinding binding = FloatPickerTextPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.editText.setText(pinString.getValue());

        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                newPinString.setValue(s == null ? "" : s.toString());
            }
        });

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinString.setValue(newPinString.getValue());
                callback.onComplete();
            }
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void show() {
        EasyFloat.with(MainApplication.getInstance().getService())
                .setLayout(this)
                .setTag(tag)
                .setDragEnable(true)
                .setCallback(floatCallback)
                .setAnimator(null)
                .hasEditText(true)
                .show();
    }
}
