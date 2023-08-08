package top.bogey.touch_tool_pro.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.databinding.FloatPickerTouchPreviewBinding;
import top.bogey.touch_tool_pro.service.MainAccessibilityService;
import top.bogey.touch_tool_pro.utils.easy_float.EasyFloat;


@SuppressLint("ViewConstructor")
public class TouchPickerFloatPreview extends BasePickerFloatView {
    private final PinTouch newPinTouch;

    @SuppressLint("DefaultLocale")
    public TouchPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinTouch pinTouch) {
        super(context, callback);
        newPinTouch = (PinTouch) pinTouch.copy();

        FloatPickerTouchPreviewBinding binding = FloatPickerTouchPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.pathView.setPaths(pinTouch.getPaths(context));

        binding.pickerButton.setOnClickListener(v -> new TouchPickerFloatView(context, () -> binding.pathView.setPaths(newPinTouch.getPaths(context)), newPinTouch).show());

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinTouch.setRecords(context, newPinTouch);
                callback.onComplete();
            }
            dismiss();
        });

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    service.runGesture(newPinTouch.getStrokeList(service, 0), null);
                } else {
                    service.runGesture(newPinTouch.getStrokes(service, 0), null);
                }
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
                .show();
    }
}
