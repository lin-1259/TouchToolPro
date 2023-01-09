package top.bogey.touch_tool.ui.card.pin_widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinAdd;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.databinding.PinWidgetAddBinding;
import top.bogey.touch_tool.ui.card.BaseCard;
import top.bogey.touch_tool.ui.custom.BindingView;

public class PinWidgetAdd extends BindingView<PinWidgetAddBinding> {
    private final PinAdd pinAdd;

    public PinWidgetAdd(@NonNull Context context, PinAdd pinAdd, BaseCard<? extends BaseAction> card) {
        this(context, null, pinAdd, card);
    }

    public PinWidgetAdd(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, new PinAdd((Pin<? extends PinObject>) null), null);
    }

    public PinWidgetAdd(@NonNull Context context, @Nullable AttributeSet attrs, PinAdd pinAdd, BaseCard<? extends BaseAction> card) {
        super(context, attrs, PinWidgetAddBinding.class);
        if (pinAdd == null) throw new RuntimeException("不是有效的引用");
        this.pinAdd = pinAdd;

        binding.addButton.setOnClickListener(v -> {
            Pin<? extends PinObject> copyPin = pinAdd.getPin().copy(true);
            card.addMorePinView(copyPin);
        });
    }
}
