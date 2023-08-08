package top.bogey.touch_tool_pro.bean.base;

import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;

public interface VariableSaveChangedListener {
    void onCreated(String key, PinValue value);

    void onChanged(String key, PinValue value);

    void onRemoved(String key, PinValue value);
}
