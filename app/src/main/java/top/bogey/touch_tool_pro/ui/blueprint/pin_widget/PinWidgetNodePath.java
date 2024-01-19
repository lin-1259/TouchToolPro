package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.databinding.PinWidgetNodePathBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.NodePickerFloatPreview;
import top.bogey.touch_tool_pro.ui.picker.PickerCallback;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetNodePath extends PinWidget<PinNodePath> {
    private final PinWidgetNodePathBinding binding;

    public PinWidgetNodePath(@NonNull Context context, ActionCard<?> card, PinView pinView, PinNodePath pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetNodePathBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);
        binding.editText.setText(String.valueOf(pinObject.getValue()));
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                String path = s.toString();
                pinObject.setValue(path);
            }
        });

        binding.pickButton.setOnClickListener(v -> new NodePickerFloatPreview(context, new PickerCallback() {
            @Override
            public void onComplete() {
                binding.editText.setText(pinObject.getValue());
            }
        }, pinObject).show());
    }

    @Override
    public void initCustom() {

    }
}
