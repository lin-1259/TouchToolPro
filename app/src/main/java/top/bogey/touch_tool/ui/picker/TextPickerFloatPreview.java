package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.databinding.FloatPickerTextPreviewBinding;
import top.bogey.touch_tool.utils.TextChangedListener;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class TextPickerFloatPreview extends BasePickerFloatView {
    private final PinString newPinString;

    @SuppressLint("DefaultLocale")
    public TextPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinString pinString) {
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
