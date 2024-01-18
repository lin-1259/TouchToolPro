package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
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
                refreshDynamicPin(path);
            }
        });

        binding.pickButton.setOnClickListener(v -> new NodePickerFloatPreview(context, new PickerCallback() {
            @Override
            public void onComplete() {
                binding.editText.setText(pinObject.getValue());
            }
        }, pinObject).show());
    }

    private void refreshDynamicPin(String path) {
        Pattern pattern = Pattern.compile("\\[\\{(\\S*)\\}]");
        Matcher matcher = pattern.matcher(path);
        HashSet<String> keys = new HashSet<>();
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        ArrayList<Pin> removePins = new ArrayList<>();

        for (Pin pin : card.getAction().getPins()) {
            if (pin.isRemoveAble()) {
                String title = pin.getTitle();
                if (keys.isEmpty()) {
                    removePins.add(pin);
                } else if (!keys.remove(title)) {
                    removePins.add(pin);
                }
            }
        }
        removePins.forEach(card::removePin);

        for (String key : keys) {
            Pin paramsPin = new Pin(new PinInteger(), 0, false, true);
            paramsPin.setTitle(key);
            card.addPin(paramsPin, 0);
        }
    }

    @Override
    public void initCustom() {

    }
}
