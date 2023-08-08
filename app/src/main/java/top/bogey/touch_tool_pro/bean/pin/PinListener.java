package top.bogey.touch_tool_pro.bean.pin;

import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;

public interface PinListener {
    // 连线了
    void onLinked(Pin linkedPin);

    // 断开连线了
    void onUnlink(Pin unlinkedPin);

    // 针脚值变更了
    void onValueChanged(PinObject value);

    //标题变更
    void onTitleChanged(String title);
}
