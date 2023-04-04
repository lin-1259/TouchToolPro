package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.databinding.PinWidgetPointBinding;
import top.bogey.touch_tool.ui.custom.BindingView;
import top.bogey.touch_tool.utils.TextChangedListener;

public class PinWidgetPoint extends BindingView<PinWidgetPointBinding> {

    public PinWidgetPoint(@NonNull Context context, PinPoint pinPoint) {
        this(context, null, pinPoint);
    }

    public PinWidgetPoint(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinPoint());
    }

    public PinWidgetPoint(@NonNull Context context, @Nullable AttributeSet attrs, PinPoint pinPoint) {
        super(context, attrs, PinWidgetPointBinding.class);
        if (pinPoint == null) throw new RuntimeException("不是有效的引用");

        binding.xEdit.setText(String.valueOf(pinPoint.getX()));
        binding.yEdit.setText(String.valueOf(pinPoint.getY()));

        binding.xEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0)
                    try {
                        pinPoint.setX(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException ignored) {
                        pinPoint.setX(0);
                    }
                else pinPoint.setX(0);
            }
        });

        binding.yEdit.addTextChangedListener(new TextChangedListener() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0)
                    try {
                        pinPoint.setY(Integer.parseInt(s.toString()));
                    } catch (NumberFormatException ignored) {
                        pinPoint.setY(0);
                    }
                else pinPoint.setY(0);
            }
        });
    }
}
