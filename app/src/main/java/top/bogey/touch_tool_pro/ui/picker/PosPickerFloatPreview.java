package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.databinding.FloatPickerPosPreviewBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.TextChangedListener;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;


@SuppressLint("ViewConstructor")
public class PosPickerFloatPreview extends BasePickerFloatView {
    private final PinPoint newPinPoint;

    @SuppressLint("DefaultLocale")
    public PosPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinPoint pinPoint) {
        super(context, callback);
        newPinPoint = (PinPoint) pinPoint.copy();

        FloatPickerPosPreviewBinding binding = FloatPickerPosPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.xEdit.setText(String.valueOf(pinPoint.getX(context)));
        binding.yEdit.setText(String.valueOf(pinPoint.getY(context)));

        binding.xEdit.addTextChangedListener(new TextChangedListener(){
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) newPinPoint.setPoint(context, Integer.parseInt(s.toString()), newPinPoint.getY(context));
            }
        });

        binding.yEdit.addTextChangedListener(new TextChangedListener(){
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) newPinPoint.setPoint(context, newPinPoint.getX(context), Integer.parseInt(s.toString()));
            }
        });

        binding.pickerButton.setOnClickListener(v -> new PosPickerFloatView(context, () -> {
            binding.xEdit.setText(String.valueOf(newPinPoint.getX(context)));
            binding.yEdit.setText(String.valueOf(newPinPoint.getY(context)));
        }, newPinPoint).show());

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinPoint.setPoint(context, newPinPoint.getX(context), newPinPoint.getY(context));
                callback.onComplete();
            }
            dismiss();
        });

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                service.runGesture(newPinPoint.getX(context), newPinPoint.getY(context), 100, null);
                service.showTouch(newPinPoint.getX(context), newPinPoint.getY(context));
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
