package top.bogey.touch_tool_pro.bean.pin;

import androidx.annotation.StringRes;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidget;

public class PinConfigInfo {
    private final @StringRes int title;
    private final Class<? extends PinObject> pinClass;
    private final Class<? extends PinWidget<? extends PinObject>> pinWidgetClass;
    private final boolean canCustom;

    public PinConfigInfo() {
        title = 0;
        pinClass = null;
        pinWidgetClass = null;
        canCustom = false;
    }

    public PinConfigInfo(int title, Class<? extends PinObject> pinClass, Class<? extends PinWidget<? extends PinObject>> pinWidgetClass) {
        this.title = title;
        this.pinClass = pinClass;
        this.pinWidgetClass = pinWidgetClass;
        canCustom = true;
    }

    public PinConfigInfo(int title, Class<? extends PinObject> pinClass, Class<? extends PinWidget<? extends PinObject>> pinWidgetClass, boolean canCustom) {
        this.title = title;
        this.pinClass = pinClass;
        this.pinWidgetClass = pinWidgetClass;
        this.canCustom = canCustom;
    }

    public String getTitle() {
        if (title != 0) return MainApplication.getInstance().getString(title);
        return "";
    }

    public Class<? extends PinObject> getPinClass() {
        return pinClass;
    }

    public Class<? extends PinWidget<? extends PinObject>> getPinWidgetClass() {
        return pinWidgetClass;
    }

    public boolean isCanCustom() {
        return canCustom;
    }
}
