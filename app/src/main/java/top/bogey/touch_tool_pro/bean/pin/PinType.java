package top.bogey.touch_tool_pro.bean.pin;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinFloat;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinLong;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArray;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetAdd;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetApp;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetArea;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetArray;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetBoolean;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetColor;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetExecute;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetFloat;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetImage;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetInteger;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetLong;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetNodePath;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetPoint;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetSpinner;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetString;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetTask;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetTouch;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetValueArea;

public enum PinType {
    OBJECT,

    EXECUTE,

    ADD,

    VALUE,
    VALUE_ARRAY,

    BOOLEAN,

    INT,
    VALUE_AREA,

    POINT,
    AREA,
    TOUCH,

    LONG,
    FLOAT,

    STRING,
    SPINNER,

    NODE,
    NODE_PATH,

    TASK,
    APP,

    IMAGE,
    COLOR;

    public PinConfigInfo getConfig() {
        return switch (this) {
            case EXECUTE -> new PinConfigInfo(R.string.pin_execute, PinExecute.class, PinWidgetExecute.class, false);
            case ADD -> new PinConfigInfo(R.string.pin_value, PinAdd.class, PinWidgetAdd.class, false);
            case VALUE -> new PinConfigInfo(R.string.pin_value, PinValue.class, null);
            case VALUE_ARRAY -> new PinConfigInfo(R.string.pin_value_array, PinValueArray.class, PinWidgetArray.class);
            case BOOLEAN -> new PinConfigInfo(R.string.pin_boolean, PinBoolean.class, PinWidgetBoolean.class);
            case INT -> new PinConfigInfo(R.string.pin_int, PinInteger.class, PinWidgetInteger.class);
            case VALUE_AREA -> new PinConfigInfo(R.string.pin_value_area, PinValueArea.class, PinWidgetValueArea.class);
            case POINT -> new PinConfigInfo(R.string.pin_point, PinPoint.class, PinWidgetPoint.class);
            case AREA -> new PinConfigInfo(R.string.pin_area, PinArea.class, PinWidgetArea.class);
            case TOUCH -> new PinConfigInfo(R.string.pin_touch, PinTouch.class, PinWidgetTouch.class);
            case LONG -> new PinConfigInfo(R.string.pin_long, PinLong.class, PinWidgetLong.class);
            case FLOAT -> new PinConfigInfo(R.string.pin_float, PinFloat.class, PinWidgetFloat.class);
            case STRING -> new PinConfigInfo(R.string.pin_string, PinString.class, PinWidgetString.class);
            case SPINNER -> new PinConfigInfo(R.string.pin_spinner, PinSpinner.class, PinWidgetSpinner.class);
            case NODE -> new PinConfigInfo(R.string.pin_node, PinNode.class, null);
            case NODE_PATH -> new PinConfigInfo(R.string.pin_node_path, PinNodePath.class, PinWidgetNodePath.class);
            case TASK -> new PinConfigInfo(R.string.pin_task, PinTask.class, PinWidgetTask.class);
            case APP -> new PinConfigInfo(R.string.pin_app, PinApplication.class, PinWidgetApp.class);
            case IMAGE -> new PinConfigInfo(R.string.pin_image, PinImage.class, PinWidgetImage.class);
            case COLOR -> new PinConfigInfo(R.string.pin_color, PinColor.class, PinWidgetColor.class);
            default -> new PinConfigInfo();
        };
    }
}
