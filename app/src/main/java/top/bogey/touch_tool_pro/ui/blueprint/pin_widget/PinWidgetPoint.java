package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.databinding.PinWidgetPointBinding;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;
import top.bogey.touch_tool_pro.ui.picker.PickerCallback;
import top.bogey.touch_tool_pro.ui.picker.PosPickerFloatPreview;
import top.bogey.touch_tool_pro.utils.TextChangedListener;

@SuppressLint("ViewConstructor")
public class PinWidgetPoint extends PinWidget<PinPoint> {
    private final PinWidgetPointBinding binding;

    public PinWidgetPoint(@NonNull Context context, ActionCard<?> card, PinView pinView, PinPoint pinObject, boolean custom) {
        super(context, card, pinView, pinObject, custom);
        binding = PinWidgetPointBinding.inflate(LayoutInflater.from(context), this, true);
        init();
    }

    @Override
    public void initBase() {
        binding.xEdit.setText(String.valueOf(pinObject.getX(context)));
        binding.yEdit.setText(String.valueOf(pinObject.getY(context)));

        binding.xEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int newX;
                try {
                    newX = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    newX = pinObject.getX(context);
                }
                if (newX == pinObject.getX(context)) return;
                pinObject.setPoint(context, newX, pinObject.getY(context));
            }
        });

        binding.yEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                int newY;
                try {
                    newY = Integer.parseInt(s.toString());
                } catch (NumberFormatException ignored) {
                    newY = pinObject.getY(context);
                }
                if (newY == pinObject.getY(context)) return;
                pinObject.setPoint(context, pinObject.getX(context), newY);
            }
        });

        binding.pickButton.setOnClickListener(v -> new PosPickerFloatPreview(context, new PickerCallback(){
            @Override
            public void onComplete() {
                binding.xEdit.setText(String.valueOf(pinObject.getX(context)));
                binding.yEdit.setText(String.valueOf(pinObject.getY(context)));
            }
        }, pinObject).show());
    }

    @Override
    public void initCustom() {

    }
}
