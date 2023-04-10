package top.bogey.touch_tool.data.action.function;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    // 内部针脚->外部针脚
    private final HashMap<String, String> pinIdMap = new HashMap<>();

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

        pinIdMap.putAll(GsonUtils.getAsType(jsonObject, "pinIdMap", new TypeToken<HashMap<String, String>>() {}.getType(), new HashMap<>()));

        executePin = super.addPin(pinsTmp.remove(0));
        for (Pin pin : pinsTmp) {
            super.addPin(pin);
        }
    }

    @Override
    public BaseAction copy() {
        FunctionAction copy = (FunctionAction) GsonUtils.copy(this, BaseAction.class);
        copy.setId(UUID.randomUUID().toString());
        copy.getPins().forEach(pin -> {
            // 动作复制需要更新内部索引
            String pinId = UUID.randomUUID().toString();
            String remove = copy.pinIdMap.remove(pin.getId());
            if (remove != null) copy.pinIdMap.put(pinId, remove);
            pin.setId(pinId);
            pin.setActionId(copy.getId());
            pin.cleanLinks();
        });
        copy.baseFunction = baseFunction;
        copy.x = x + 1;
        copy.y = y + 1;

        return copy;
    }


    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart()) super.doAction(runnable, actionContext, pin);
        else {
            ((BaseFunction) actionContext).setEndFunction(this);
        }
    }

    @Override
    protected PinObject getPinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (tag.isStart())
            return ((BaseFunction) actionContext).getPinValue(runnable, pinIdMap.get(pin.getId()));
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
        copy.setDirection(copy.getDirection() == PinDirection.IN ? PinDirection.OUT : PinDirection.IN);
        pinIdMap.put(copy.getId(), outerPin.getId());
        super.addPin(copy);
        if (callback != null) callback.onPinAdded(copy);
        return copy;
    }

    @Override
    public Pin removePin(ActionContext context, Pin outerPin) {
        String pinId = getMappingPinId(outerPin.getId());
        Pin pin = getPinById(pinId);
        pinIdMap.remove(pinId);
        if (callback != null) callback.onPinRemoved(pin);
        return super.removePin(context, pin);
    }

    public void setPinValue(String outerPinId, PinObject value) {
        Pin pin = getPinById(getMappingPinId(outerPinId));
        pin.setValue(value.copy());
    }

    public void setPinTitle(String outerPinId, String title) {
        Pin pin = getPinById(getMappingPinId(outerPinId));
        pin.setTitle(title);
        if (callback != null) callback.onPinTitleChanged(pin);
    }

    public String getMappingPinId(String outerPinId) {
        for (Map.Entry<String, String> entry : pinIdMap.entrySet()) {
            if (entry.getValue().equals(outerPinId)) return entry.getKey();
        }
        return null;
    }

    public BaseFunction.FUNCTION_TAG getTag() {
        return tag;
    }

    public HashMap<String, String> getPinIdMap() {
        return pinIdMap;
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
