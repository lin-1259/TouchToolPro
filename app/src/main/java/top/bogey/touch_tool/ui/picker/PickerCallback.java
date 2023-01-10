package top.bogey.touch_tool.ui.picker;

import top.bogey.touch_tool.data.pin.object.PinValue;

public interface PickerCallback {
    void onComplete(PinValue pinValue);
}
