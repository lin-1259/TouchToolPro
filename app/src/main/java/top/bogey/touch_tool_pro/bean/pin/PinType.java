package top.bogey.touch_tool_pro.bean.pin;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.pins.PinAdd;
import top.bogey.touch_tool_pro.bean.pin.pins.PinApplication;
import top.bogey.touch_tool_pro.bean.pin.pins.PinArea;
import top.bogey.touch_tool_pro.bean.pin.pins.PinBoolean;
import top.bogey.touch_tool_pro.bean.pin.pins.PinColor;
import top.bogey.touch_tool_pro.bean.pin.pins.PinExecute;
import top.bogey.touch_tool_pro.bean.pin.pins.PinImage;
import top.bogey.touch_tool_pro.bean.pin.pins.PinInteger;
import top.bogey.touch_tool_pro.bean.pin.pins.PinLong;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNode;
import top.bogey.touch_tool_pro.bean.pin.pins.PinNodePath;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinPoint;
import top.bogey.touch_tool_pro.bean.pin.pins.PinSpinner;
import top.bogey.touch_tool_pro.bean.pin.pins.PinString;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTask;
import top.bogey.touch_tool_pro.bean.pin.pins.PinTouch;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValueArea;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidget;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetAdd;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetApp;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetArea;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetBoolean;
import top.bogey.touch_tool_pro.ui.blueprint.pin_widget.PinWidgetColor;
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

    public String getTitle() {
        return MainApplication.getInstance().getString(switch (this) {
            case BOOLEAN -> R.string.pin_boolean;
            case INT -> R.string.pin_int;
            case VALUE_AREA -> R.string.pin_value_area;
            case POINT -> R.string.pin_point;
            case AREA -> R.string.pin_area;
            case TOUCH -> R.string.pin_touch;
            case LONG -> R.string.pin_long;
            case STRING -> R.string.pin_string;
            case SPINNER -> R.string.pin_spinner;
            case NODE -> R.string.pin_node;
            case NODE_PATH -> R.string.pin_node_path;
            case TASK -> R.string.pin_task;
            case APP -> R.string.pin_app;
            case IMAGE -> R.string.pin_image;
            case COLOR -> R.string.pin_color;
            default -> R.string.pin_value;
        });
    }

    public boolean canCustom() {
        return switch (this) {
            case OBJECT, EXECUTE, ADD, FLOAT -> false;
            default -> true;
        };
    }

    public Class<? extends PinObject> getPinObjectClass() {
        return switch (this) {
            case EXECUTE -> PinExecute.class;
            case VALUE -> PinValue.class;

            case ADD -> PinAdd.class;
            case BOOLEAN -> PinBoolean.class;
            case INT -> PinInteger.class;
            case VALUE_AREA -> PinValueArea.class;
            case POINT -> PinPoint.class;
            case AREA -> PinArea.class;
            case TOUCH -> PinTouch.class;
            case LONG -> PinLong.class;
            case STRING -> PinString.class;
            case SPINNER -> PinSpinner.class;
            case NODE -> PinNode.class;
            case NODE_PATH -> PinNodePath.class;
            case TASK -> PinTask.class;
            case APP -> PinApplication.class;
            case IMAGE -> PinImage.class;
            case COLOR -> PinColor.class;
            default -> null;
        };
    }

    public Class<? extends PinWidget<? extends PinObject>> getPinWidgetClass() {
        return switch (this) {
            case ADD -> PinWidgetAdd.class;
            case BOOLEAN -> PinWidgetBoolean.class;
            case INT -> PinWidgetInteger.class;
            case VALUE_AREA -> PinWidgetValueArea.class;
            case POINT -> PinWidgetPoint.class;
            case AREA -> PinWidgetArea.class;
            case TOUCH -> PinWidgetTouch.class;
            case LONG -> PinWidgetLong.class;
            case STRING -> PinWidgetString.class;
            case SPINNER -> PinWidgetSpinner.class;
            case NODE_PATH -> PinWidgetNodePath.class;
            case TASK -> PinWidgetTask.class;
            case APP -> PinWidgetApp.class;
            case IMAGE -> PinWidgetImage.class;
            case COLOR -> PinWidgetColor.class;
            default -> null;
        };
    }
}
