package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.databinding.FloatPickerTouchPreviewBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class TouchPickerFloatPreview extends BasePickerFloatView {
    private final PinPath newPinPath;

    @SuppressLint("DefaultLocale")
    public TouchPickerFloatPreview(@NonNull Context context, PickerCallback callback, PinPath pinPath) {
        super(context, callback);
        newPinPath = (PinPath) pinPath.copy();

        FloatPickerTouchPreviewBinding binding = FloatPickerTouchPreviewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.pathView.setPaths(pinPath.getPaths());

        binding.pickerButton.setOnClickListener(v -> new TouchPickerFloatView(context, () -> binding.pathView.setPaths(newPinPath.getPaths()), newPinPath).show());

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                pinPath.setPaths(context, newPinPath.getPaths());
                pinPath.setOffset(newPinPath.getOffset());
                pinPath.setGravity(newPinPath.getGravity());
                pinPath.setScreen(DisplayUtils.getScreen(context));
                callback.onComplete();
            }
            dismiss();
        });

        int[] time = {100};
        binding.timeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                time[0] = (int) value;
            }
        });
        binding.timeSlider.setLabelFormatter(value -> String.format("%dms", (int) value));

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isServiceEnabled()) {
                service.runGesture(newPinPath.getRealPaths(context, false), time[0], null);
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
