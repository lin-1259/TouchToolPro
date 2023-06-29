package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.databinding.FloatPickerPosPreviewBinding;
import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.utils.TextChangedListener;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class PosPickerFloatPreview extends BasePickerFloatView {
    private final PinPoint newPinPoint;

    @SuppressLint("DefaultLocale")
    public PosPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinPoint pinPoint) {
        super(context, callback);
        newPinPoint = (PinPoint) pinPoint.copy();

        FloatPickerPosPreviewBinding binding = FloatPickerPosPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.xEdit.setText(String.valueOf(pinPoint.getX()));
        binding.yEdit.setText(String.valueOf(pinPoint.getY()));

        binding.xEdit.addTextChangedListener(new TextChangedListener(){
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) newPinPoint.setX(Integer.parseInt(s.toString()));
            }
        });

        binding.yEdit.addTextChangedListener(new TextChangedListener(){
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) newPinPoint.setY(Integer.parseInt(s.toString()));
            }
        });

        binding.pickerButton.setOnClickListener(v -> new PosPickerFloatView(context, () -> {
            binding.xEdit.setText(String.valueOf(newPinPoint.getX()));
            binding.yEdit.setText(String.valueOf(newPinPoint.getY()));
        }, newPinPoint).show());

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinPoint.setX(newPinPoint.getX());
                pinPoint.setY(newPinPoint.getY());
                callback.onComplete();
            }
            dismiss();
        });

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                service.runGesture(newPinPoint.getX(), newPinPoint.getY(), 100, null);
            }
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
