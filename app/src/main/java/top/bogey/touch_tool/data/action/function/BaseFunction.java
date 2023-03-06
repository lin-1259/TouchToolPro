package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

/* BaseFunction只是一个包装，所有针脚都是重定向到startAction和endAction去执行。
 */
public class BaseFunction extends NormalAction implements ActionContext {
    private final String functionId;
    private final HashSet<BaseAction> actions = new HashSet<>();
    private final HashMap<String, PinObject> attrs = new HashMap<>();
    private boolean justCall = false;

    private transient ActionContext outContext;
    private transient final HashSet<FunctionAction> startFunctions = new HashSet<>();
    private transient FunctionAction startFunction;
    private transient final HashSet<FunctionAction> endFunctions = new HashSet<>();
    private transient FunctionAction endFunction;

    public BaseFunction(Context context) {
        super(context, 0);
        functionId = UUID.randomUUID().toString();

        startFunction = new FunctionAction(context, FUNCTION_TAG.START, this);
        startFunctions.add(startFunction);
        actions.add(startFunction);

        // 不是 this.endFunction
        FunctionAction endFunction = new FunctionAction(context, FUNCTION_TAG.END, this);
        endFunctions.add(endFunction);
        actions.add(endFunction);
    }

    public BaseFunction(JsonObject jsonObject) {
        super(jsonObject);
        functionId = jsonObject.get("functionId").getAsString();
        justCall = jsonObject.get("justCall").getAsBoolean();

        Gson gson = TaskRepository.getInstance().getGson();
        actions.addAll(gson.fromJson(jsonObject.get("actions"), new TypeToken<HashSet<BaseAction>>() {}.getType()));
        for (BaseAction action : actions) {
            if (action instanceof FunctionAction) {
                FunctionAction function = (FunctionAction) action;
                function.setBaseFunction(this);
                if (function.getTag().isStart()) {
                    startFunctions.add(function);
                    // 开始动作是唯一的
                    startFunction = function;
                } else endFunctions.add(function);
            }
        }

        JsonElement attrsElement = jsonObject.get("attrs");
        if (attrsElement != null) attrs.putAll(gson.fromJson(attrsElement, new TypeToken<HashMap<String, PinObject>>() {}.getType()));

        for (Pin pin : tmpPins) {
            // 不能直接调用自身的添加
            super.addPin(pin);
            // 自身的输入针脚的值需要使用开始动作的输出针脚的值
            if (!pin.getDirection().isOut()) {
                Pin innerPin = startFunction.getPinById(startFunction.getMappingPinId(pin.getId()));
                pin.setValue(innerPin.getValue());
            }
        }
    }

    @Override
    public BaseAction copy() {
        Gson gson = TaskRepository.getInstance().getGson();
        String json = gson.toJson(this);
        BaseFunction copy = (BaseFunction) gson.fromJson(json, BaseAction.class);
        copy.setId(UUID.randomUUID().toString());

        copy.getPins().forEach(pin -> {
            String pinId = UUID.randomUUID().toString();
            // 复制的话需要更新内部动作的外部针脚索引
            for (FunctionAction function : (pin.getDirection().isOut() ? copy.endFunctions : copy.startFunctions)) {
                function.getPinIdMap().put(function.getMappingPinId(pin.getId()), pinId);
            }
            pin.setId(pinId);
            pin.setActionId(copy.getId());
            pin.cleanLinks();
        });
        copy.x = x + 1;
        copy.y = y + 1;
        return copy;
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        endFunction = null;
        outContext = actionContext;
        startFunction.doAction(runnable, this, startFunction.getExecutePin());

        if (!justCall) {
            doNextAction(runnable, actionContext, outPin);
        }
    }

    @Override
    protected void calculatePinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (justCall) {
            doAction(runnable, actionContext, inPin);
        }
    }

    @Override
    protected PinObject getPinValue(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        if (pin.getDirection().isOut()) {
            calculatePinValue(runnable, actionContext, pin);
            if (endFunction == null) return pin.getValue();
            else {
                Pin pinById = endFunction.getPinById(endFunction.getMappingPinId(pin.getId()));
                outContext = actionContext;
                return endFunction.getPinValue(runnable, this, pinById);
            }
        } else {
            return super.getPinValue(runnable, actionContext, pin);
        }
    }

    public PinObject getPinValue(TaskRunnable runnable, String pinId) {
        Pin pin = getPinById(pinId);
        return getPinValue(runnable, outContext, pin);
    }

    @Override
    public ArrayList<Pin> getShowPins() {
        ArrayList<Pin> pins = new ArrayList<>(getPins());
        if (justCall) {
            pins.remove(inPin);
            pins.remove(outPin);
        }
        return pins;
    }

    @Override
    public Pin addPin(Pin pin) {
        super.addPin(pin);
        if (startFunctions != null && endFunctions != null) {
            // 这个pin是加给自身的, 自身的输出就是endFunctions的输入，所以function内反转了pin方向和针脚类型
            for (FunctionAction function : (pin.getDirection().isOut() ? endFunctions : startFunctions)) {
                function.addPin(pin);
            }
        }
        return pin;
    }

    @Override
    public Pin removePin(Pin pin) {
        Pin removePin = super.removePin(pin);
        if (removePin == null) return null;
        for (FunctionAction function : (pin.getDirection().isOut() ? endFunctions : startFunctions)) {
            function.removePin(pin);
        }
        return removePin;
    }

    public void setPinValue(FunctionAction functionAction, Pin innerPin, PinObject value) {
        String pinId = functionAction.getPinIdMap().get(innerPin.getId());
        Pin pin = getPinById(pinId);
        pin.setValue(value.copy());

        for (FunctionAction function : (functionAction.getTag().isStart() ? startFunctions : endFunctions)) {
            function.setPinValue(pinId, value);
        }
    }

    public void setPinTitle(FunctionAction functionAction, Pin innerPin, String title) {
        String pinId = functionAction.getPinIdMap().get(innerPin.getId());
        Pin pin = getPinById(pinId);
        pin.setTitle(title);

        for (FunctionAction function : (functionAction.getTag().isStart() ? startFunctions : endFunctions)) {
            function.setPinTitle(pinId, title);
        }
    }

    public void setEndFunction(FunctionAction endFunction) {
        this.endFunction = endFunction;
    }

    public boolean isJustCall() {
        return justCall;
    }

    public void setJustCall(boolean justCall) {
        this.justCall = justCall;
    }

    public String getFunctionId() {
        return functionId;
    }

    @Override
    public HashSet<BaseAction> getActions() {
        return actions;
    }

    @Override
    public void addAction(BaseAction action) {
        actions.add(action);
        // 开始动作有且仅有一个，所以加到结束里去
        if (action instanceof FunctionAction) {
            endFunctions.add((FunctionAction) action);
        }
    }

    @Override
    public void removeAction(BaseAction action) {
        for (BaseAction baseAction : actions) {
            if (baseAction.getId().equals(action.getId())) {
                actions.remove(baseAction);
                break;
            }
        }
        // 开始动作有且仅有一个，所以到结束里去移除
        if (action instanceof FunctionAction) {
            for (FunctionAction function : endFunctions) {
                if (function.getId().equals(action.getId())) {
                    endFunctions.remove(function);
                    break;
                }
            }
        }
    }

    @Override
    public BaseAction getActionById(String id) {
        for (BaseAction action : actions) {
            if (action.getId().equals(id)) return action;
        }
        return null;
    }

    @Override
    public ArrayList<BaseAction> getActionsByClass(Class<? extends BaseAction> actionClass) {
        ArrayList<BaseAction> actions = new ArrayList<>();
        for (BaseAction action : this.actions) {
            if (actionClass.isInstance(action)) {
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    public HashMap<String, PinObject> getAttrs() {
        return attrs;
    }

    @Override
    public void addAttr(String key, PinObject value) {
        attrs.put(key, value);
    }

    @Override
    public void removeAttr(String key) {
        attrs.remove(key);
    }

    @Override
    public PinObject getAttr(String key) {
        return attrs.get(key);
    }

    @Override
    public boolean isReturned() {
        return endFunction != null;
    }

    public enum FUNCTION_TAG {
        START, END;

        public boolean isStart() {
            return this == START;
        }
    }
}
