package top.bogey.touch_tool.data.pin.object;

import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import top.bogey.touch_tool.R;

public class PinNodeInfo extends PinObject{
    private transient AccessibilityNodeInfo nodeInfo;

    public PinNodeInfo() {
        super();
    }

    public PinNodeInfo(JsonObject jsonObject) {
        super(jsonObject);
    }

    public AccessibilityNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(AccessibilityNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    @Override
    public int getPinColor(Context context) {
        return context.getColor(R.color.NodePinColor);
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(nodeInfo);
    }
}
