package top.bogey.touch_tool.data.action.start;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinString;

public class OutStartAction extends StartAction {
    private transient Pin idPin = new Pin(new PinString(getId()), R.string.action_out_start_subtitle_id, PinSubType.URL);
    private transient Pin shortcutPin = new Pin(new PinString(getId()), R.string.action_out_start_subtitle_shortcut, PinSubType.SHORTCUT);

    public OutStartAction() {
        super(R.string.action_out_start_title);
        idPin = addPin(idPin);
        shortcutPin = addPin(shortcutPin);
    }

    public OutStartAction(JsonObject jsonObject) {
        super(R.string.action_out_start_title, jsonObject);
        idPin = reAddPin(idPin);
        shortcutPin = reAddPin(shortcutPin);
        setId(getId());
    }

    // 更新针脚归属和信息
    @Override
    public void setId(String id) {
        super.setId(id);
        ((PinString) idPin.getValue()).setValue(id);
        ((PinString) shortcutPin.getValue()).setValue(id);
    }
}
