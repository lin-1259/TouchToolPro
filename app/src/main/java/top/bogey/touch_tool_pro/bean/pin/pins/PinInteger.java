package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class PinInteger extends PinNumber<Integer> {

    public PinInteger() {
        super(PinType.INT, 0);
    }

    public PinInteger(int value) {
        super(PinType.INT, value);
    }

    public PinInteger(JsonObject jsonObject) {
        super(jsonObject);
        value = GsonUtils.getAsInt(jsonObject, "value", 0);
    }

    @Override
    public boolean cast(String value) {
        try {
            this.value = Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.IntegerPinColor);
    }
}
