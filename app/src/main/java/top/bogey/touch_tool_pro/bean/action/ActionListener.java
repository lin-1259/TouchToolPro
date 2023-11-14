package top.bogey.touch_tool_pro.bean.action;

import top.bogey.touch_tool_pro.bean.pin.Pin;

public interface ActionListener {
    void onPinAdded(Pin pin);

    void onPinRemoved(Pin pin);

    void onPinChanged(Pin pin);
}
