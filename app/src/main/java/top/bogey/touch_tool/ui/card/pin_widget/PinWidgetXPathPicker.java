package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinXPath;
import top.bogey.touch_tool.databinding.PinWidgetXpathBinding;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.ui.picker.WidgetPickerFloatPreview;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetXPathPicker extends BindingView<PinWidgetXpathBinding> {

    public PinWidgetXPathPicker(@NonNull Context context, PinXPath pinXPath, BaseCard<?> card) {
        this(context, null, pinXPath, card);
    }

    public PinWidgetXPathPicker(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinXPath(), null);
    }

    public PinWidgetXPathPicker(@NonNull Context context, @Nullable AttributeSet attrs, PinXPath pinXPath, BaseCard<?> card) {
        super(context, attrs, PinWidgetXpathBinding.class);
        if (pinXPath == null) throw new RuntimeException("不是有效的引用");

        binding.editText.setSaveEnabled(false);
        binding.editText.setSaveFromParentEnabled(false);
        binding.editText.setText(pinXPath.getPath());
        binding.editText.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) pinXPath.setPath((String) null);
                else {
                    String string = s.toString();
                    pinXPath.setPath(string);
                    refreshDynamicPin(card, string);
                }
            }
        });

        binding.pickButton.setOnClickListener(v -> new WidgetPickerFloatPreview(context, () -> binding.editText.setText(pinXPath.getPath()), pinXPath).show());
    }

    private void refreshDynamicPin(BaseCard<?> card, String string) {
        Pattern pattern = Pattern.compile("\\[\\{(\\S*?)\\}]");
        Matcher matcher = pattern.matcher(string);
        HashSet<String> keys = new HashSet<>();
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        ArrayList<Pin> removePins = new ArrayList<>();

        for (Pin pin : card.getAction().getPins()) {
            if (pin.isRemoveAble()) {
                String title = pin.getTitle(null);
                if (keys.isEmpty()) {
                    removePins.add(pin);
                } else if (!keys.remove(title)) {
                    removePins.add(pin);
                }
            }
        }
        removePins.forEach(card::removeMorePinView);

        for (String key : keys) {
            Pin paramsPin = new Pin(new PinInteger(), 0, PinDirection.IN, PinSubType.NORMAL, true);
            paramsPin.setTitle(key);
            card.addMorePinView(paramsPin, 0);
        }
    }
}
