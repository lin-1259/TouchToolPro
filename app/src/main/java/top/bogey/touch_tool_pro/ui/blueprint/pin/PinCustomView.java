package top.bogey.touch_tool_pro.ui.blueprint.pin;

import android.content.Context;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;

public abstract class PinCustomView extends PinView{
    protected final Pin functionPin;

    public PinCustomView(@NonNull Context context, ActionCard<?> card, Pin pin) {
        super(context, card, pin, true);
        functionPin = ((Function) card.getFunctionContext()).getAction().getPinByUid(pin.getUid());
    }

    public Pin getFunctionPin() {
        return functionPin;
    }
}
