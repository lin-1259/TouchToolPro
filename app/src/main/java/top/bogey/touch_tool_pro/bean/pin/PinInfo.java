package top.bogey.touch_tool_pro.bean.pin;

import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;

public class PinInfo {
    public Class<? extends PinObject> pinClass;
    public boolean out;

    public PinInfo(Class<? extends PinObject> pinClass) {
        this(pinClass, false);
    }

    public PinInfo(Class<? extends PinObject> pinClass, boolean out) {
        this.pinClass = pinClass;
        this.out = out;
    }
}
