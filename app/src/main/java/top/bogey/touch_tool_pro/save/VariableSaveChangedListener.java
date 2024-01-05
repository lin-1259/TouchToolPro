package top.bogey.touch_tool_pro.save;

import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;

public interface VariableSaveChangedListener {
    void onCreated(String key, PinValue value);

    void onChanged(String key, PinValue value);

    void onRemoved(String key, PinValue value);
}
