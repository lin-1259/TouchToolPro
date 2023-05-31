package top.bogey.touch_tool.ui.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import java.util.List;

import top.bogey.touch_tool.service.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinValue;
import top.bogey.touch_tool.databinding.FloatPickerImagePreviewBinding;
import top.bogey.touch_tool.utils.DisplayUtils;
import top.bogey.touch_tool.utils.easy_float.EasyFloat;

@SuppressLint("ViewConstructor")
public class ImagePickerFloatPreview extends BasePickerFloatView {

    private PinImage pinImage;
    private PinColor pinColor;

    @SuppressLint("DefaultLocale")
    public ImagePickerFloatPreview(@NonNull Context context, PickerCallback callback, PinValue pinValue) {
        super(context, callback);
        boolean isImage = pinValue instanceof PinImage;

        FloatPickerImagePreviewBinding binding = FloatPickerImagePreviewBinding.inflate(LayoutInflater.from(context), this, true);

        if (isImage) {
            pinImage = (PinImage) pinValue.copy();
            binding.current.setImageBitmap(pinImage.getBitmap());
            binding.title.setText(R.string.picker_image_preview_title);
            binding.pickerButton.setIconResource(R.drawable.icon_image);
            binding.pickerButton.setOnClickListener(v -> {


                new ImagePickerFloatView(context, () -> binding.current.setImageBitmap(pinImage.getBitmap()), pinImage).show();
            });
            binding.playButton.setVisibility(GONE);
        } else {
            pinColor = (PinColor) pinValue.copy();
            binding.current.setBackgroundColor(DisplayUtils.getColorFromHsv(pinColor.getColor()));
            binding.title.setText(R.string.picker_color_preview_title);
            binding.pickerButton.setIconResource(R.drawable.icon_color);
            binding.pickerButton.setOnClickListener(v -> new ColorPickerFloatView(context, () -> binding.current.setBackgroundColor(DisplayUtils.getColorFromHsv(pinColor.getColor())), pinColor).show());
            binding.playBox.setVisibility(GONE);
        }

        binding.saveButton.setOnClickListener(v -> {
            if (callback != null) {
                if (isImage) {
                    if (pinImage != null) {
                        PinImage image = (PinImage) pinValue;
                        Bitmap bitmap = pinImage.getBitmap();
                        image.setBitmap(context, bitmap, pinImage.getArea(context));
                    }
                } else {
                    if (pinColor != null) {
                        PinColor color = (PinColor) pinValue;
                        color.setColor(pinColor.getColor());
                        color.setMinSize(pinColor.getMinSize(context));
                        color.setMaxSize(pinColor.getMaxSize(context));
                        color.setScreen(DisplayUtils.getScreen(context));
                        color.setArea(pinColor.getArea(context));
                    }
                }
                callback.onComplete();
            }
            dismiss();
        });

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.playButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isCaptureEnabled()) {
                List<Rect> rectList = service.binder.matchColor(pinColor.getColor(), pinColor.getArea(context));
                if (rectList != null && rectList.size() > 0) {
                    Rect rect = rectList.get(0);
                    service.runGesture(rect.centerX(), rect.centerY(), 100, null);
                }
            }
        });

        int[] match = {85};
        binding.timeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                match[0] = (int) value;
            }
        });
        binding.timeSlider.setLabelFormatter(value -> String.format("%d%%", (int) value));
        binding.matchButton.setOnClickListener(v -> {
            MainAccessibilityService service = MainApplication.getInstance().getService();
            if (service != null && service.isCaptureEnabled()) {
                Rect rect = service.binder.matchImage(pinImage.getScaleBitmap(context), match[0], pinImage.getArea(context));
                if (rect != null) {
                    service.runGesture(rect.centerX(), rect.centerY(), 100, null);
                }
            }
        });
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
