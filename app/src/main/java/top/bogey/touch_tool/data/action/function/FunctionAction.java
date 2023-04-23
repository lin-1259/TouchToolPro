package top.bogey.touch_tool.data.action.function;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import top.bogey.touch_tool.MainAccessibilityService;
import top.bogey.touch_tool.MainApplication;
import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.GsonUtils;

public class FunctionAction extends BaseAction {
    private final BaseFunction.FUNCTION_TAG tag;
    // 内部针脚->外部针脚。仅在增删的时候会修改，复制的时候不会变
    private final HashMap<String, String> pinUidMap = new HashMap<>();

    private transient BaseFunction baseFunction;
    private transient final Pin executePin;
    private transient FunctionChangedCallback callback;

    public FunctionAction(BaseFunction.FUNCTION_TAG tag, BaseFunction baseFunction) {
        super(tag.isStart() ? R.string.function_start : R.string.function_end);
        this.tag = tag;
        if (tag.isStart()) {
            executePin = super.addPin(new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT));
        } else {
            executePin = super.addPin(new Pin(new PinExecute()));
        }
        this.baseFunction = baseFunction;
    }

    public FunctionAction(JsonObject jsonObject) {
        super(0, jsonObject);
        tag = BaseFunction.FUNCTION_TAG.valueOf(GsonUtils.getAsString(jsonObject, "tag", BaseFunction.FUNCTION_TAG.START.name()));
        setTitleId(tag.isStart() ? R.string.function_start : R.string.function_end);

        pinUidMap.putAll(GsonUtils.getAsType(jsonObject, "pinUidMap", new TypeToken<HashMap<String, String>>() {}.getType(), new HashMap<>()));

        if (tag.isStart()) {
            executePin = reAddPin(new Pin(new PinExecute(), R.string.action_subtitle_execute, PinDirection.OUT));
        } else {
            executePin = reAddPin(new Pin(new PinExecute()));
        }

        for (Pin pin : pinsTmp) {
            super.addPin(pin);
        }
    }

    @Override
    public BaseAction copy() {
        FunctionAction copy = (FunctionAction) super.copy();
        copy.baseFunction = baseFunction;
        return copy;
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart()) super.doAction(runnable, actionContext, pin);
        else {
            ((BaseFunction) actionContext).doEndFunction(this, pin);
        }
    }

    @Override
    protected PinObject getPinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart())
            return ((BaseFunction) actionContext).getPinValue(runnable, pinUidMap.get(pin.getUid()));
        else return super.getPinValue(runnable, actionContext, pin);
    }

    @Override
    public String getDes() {
        MainAccessibilityService service = MainApplication.getInstance().getService();
        if (service == null) return null;
        return baseFunction.getTitle(service);
    }

    @Override
    public void setDes(String des) {
        if (tag.isStart()) baseFunction.setTitle(des);
    }

    @Override
    public Pin addPin(Pin outerPin) {
        Pin copy = outerPin.copy(true);
        // 针脚方向需要与外部的相反
        copy.setDirection(copy.getDirection().isOut() ? PinDirection.IN : PinDirection.OUT);
        pinUidMap.put(copy.getUid(), outerPin.getUid());
        super.addPin(copy);
        if (callback != null) callback.onPinAdded(copy);
        return copy;
    }

    @Override
    public Pin removePin(ActionContext context, Pin outerPin) {
        String pinUid = getMappingPinUid(outerPin.getUid());
        Pin pin = getPinByUid(pinUid);
        pinUidMap.remove(pinUid);
        if (callback != null) callback.onPinRemoved(pin);
        return super.removePin(context, pin);
    }

    public void setPinValue(String outerPinUid, PinObject value) {
        Pin pin = getPinByUid(getMappingPinUid(outerPinUid));
        pin.setValue(value.copy());
        pin.removeLinks(baseFunction);
    }

    public void setPinTitle(String outerPinUid, String title) {
        Pin pin = getPinByUid(getMappingPinUid(outerPinUid));
        pin.setTitle(title);
        if (callback != null) callback.onPinTitleChanged(pin);
    }

    public String getMappingPinUid(String outerPinUid) {
        for (Map.Entry<String, String> entry : pinUidMap.entrySet()) {
            if (entry.getValue().equals(outerPinUid)) return entry.getKey();
        }
        return null;
    }

    public HashMap<String, String> getPinUidMap() {
        return pinUidMap;
    }

    public BaseFunction.FUNCTION_TAG getTag() {
        return tag;
    }

    public Pin getExecutePin() {
        return executePin;
    }

    public void setCallback(FunctionChangedCallback callback) {
        this.callback = callback;
    }

    public void setBaseFunction(BaseFunction baseFunction) {
        this.baseFunction = baseFunction;
    }

    public interface FunctionChangedCallback {
        void onPinAdded(Pin pin);

        void onPinRemoved(Pin pin);

        void onPinTitleChanged(Pin pin);
    }
}
