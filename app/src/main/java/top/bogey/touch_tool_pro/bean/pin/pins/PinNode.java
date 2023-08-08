package top.bogey.touch_tool_pro.bean.pin.pins;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.pin.PinType;

public class PinNode extends PinValue{
    private AccessibilityNodeInfo node;

    public PinNode() {
        super(PinType.NODE);
    }

    public PinNode(AccessibilityNodeInfo node) {
        this();
        this.node = node;
    }

    public PinNode(JsonObject jsonObject) {
        super(jsonObject);
    }

    @NonNull
    @Override
    public String toString() {
        if (node != null) {
            CharSequence text = node.getText();
            if (text != null) {
                return text.toString();
            }
        }
        return "";
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.NodePinColor);
    }

    public AccessibilityNodeInfo getNode() {
        return node;
    }

    public void setNode(AccessibilityNodeInfo node) {
        this.node = node;
    }
}
