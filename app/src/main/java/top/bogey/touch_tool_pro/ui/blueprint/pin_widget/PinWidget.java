package top.bogey.touch_tool_pro.ui.blueprint.pin_widget;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.ui.blueprint.card.ActionCard;
import top.bogey.touch_tool_pro.ui.blueprint.pin.PinView;

public abstract class PinWidget<T extends PinObject> extends FrameLayout {
    protected final ActionCard<?> card;
    protected final PinView pinView;
    protected final T pinObject;
    protected final boolean custom;
    protected final Context context;

    public PinWidget(@NonNull Context context, ActionCard<?> card, PinView pinView, T pinObject, boolean custom) {
        super(context);
        this.context = context;
        this.card = card;
        this.pinView = pinView;
        this.pinObject = pinObject;
        this.custom = custom;
    }

    public void init() {
        initBase();
        if (custom) initCustom();
    }

    public abstract void initBase();

    public abstract void initCustom();
}
