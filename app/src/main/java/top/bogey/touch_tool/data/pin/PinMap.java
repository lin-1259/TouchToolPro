package top.bogey.touch_tool.data.pin;

import android.util.ArrayMap;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.pin.object.PinBoolean;
import top.bogey.touch_tool.data.pin.object.PinColor;
import top.bogey.touch_tool.data.pin.object.PinImage;
import top.bogey.touch_tool.data.pin.object.PinInteger;
import top.bogey.touch_tool.data.pin.object.PinNodeInfo;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.data.pin.object.PinPath;
import top.bogey.touch_tool.data.pin.object.PinPoint;
import top.bogey.touch_tool.data.pin.object.PinSelectApp;
import top.bogey.touch_tool.data.pin.object.PinString;
import top.bogey.touch_tool.data.pin.object.PinValue;
import top.bogey.touch_tool.data.pin.object.PinValueArea;
import top.bogey.touch_tool.data.pin.object.PinWidget;
import top.bogey.touch_tool.data.pin.object.PinXPath;

public class PinMap {
    private static PinMap pinMap;
    private final ArrayMap<Class<? extends PinObject>, Integer> map = new ArrayMap<>(30);

    private PinMap() {
        map.put(PinBoolean.class, R.string.pin_boolean);
        map.put(PinInteger.class, R.string.pin_int);
        map.put(PinString.class, R.string.pin_string);
        map.put(PinValueArea.class, R.string.pin_value_area);
        map.put(PinImage.class, R.string.pin_image);
        map.put(PinColor.class, R.string.pin_color);
        map.put(PinPoint.class, R.string.pin_point);
        map.put(PinPath.class, R.string.pin_path);
        map.put(PinWidget.class, R.string.pin_widget);
        map.put(PinNodeInfo.class, R.string.pin_node_info);
        map.put(PinSelectApp.class, R.string.pin_app);
        map.put(PinXPath.class, R.string.pin_xpath);
        map.put(PinValue.class, R.string.pin_value);

//        map.put(PinExecute.class, R.string.pin_execute);
    }

    public static PinMap getInstance() {
        if (pinMap == null) pinMap = new PinMap();
        return pinMap;
    }

    public ArrayMap<Class<? extends PinObject>, Integer> getMap() {
        return map;
    }
}
