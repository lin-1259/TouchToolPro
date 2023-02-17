package top.bogey.touch_tool.data.action.start;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinSubType;
import top.bogey.touch_tool.data.pin.object.PinString;

public class OutStartAction extends StartAction {
    private transient final Pin idPin;

    public OutStartAction(Context context) {
        super(context, R.string.action_out_start_title);
        idPin = addPin(new Pin(new PinString(getId()), context.getString(R.string.action_out_start_subtitle_id), PinSubType.URL));
    }

    public OutStartAction(JsonObject jsonObject) {
        super(jsonObject);
        idPin = addPin(tmpPins.remove(0));
        ((PinString) idPin.getValue()).setValue(getId());
    }
}
