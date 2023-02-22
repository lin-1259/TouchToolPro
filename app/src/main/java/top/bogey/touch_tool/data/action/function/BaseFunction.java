package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.data.pin.object.PinObject;

/* BaseFunction只是一个包装，所有针脚都是重定向到startAction和endAction去执行。
 */
public class BaseFunction extends NormalAction implements ActionContext {
    private final HashSet<BaseAction> actions = new HashSet<>();
    private final HashMap<String, String> pinIdMap = new HashMap<>();
    private final String startActionId;

    private boolean justCall = false;

    private transient ActionContext outContext;
    private transient FunctionAction endFunctionAction;

    public BaseFunction(Context context) {
        super(context, 0);
        FunctionAction startAction = new FunctionAction(context, FUNCTION_TAG.START, this);
        startActionId = startAction.getId();
        actions.add(startAction);
        actions.add(new FunctionAction(context, FUNCTION_TAG.END, this));
    }

    public BaseFunction(JsonObject jsonObject) {
        super(jsonObject);

        startActionId = jsonObject.get("startActionId").getAsString();
        justCall = jsonObject.get("justCall").getAsBoolean();

        Gson gson = TaskRepository.getInstance().getGson();
        actions.addAll(gson.fromJson(jsonObject.get("actions"), new TypeToken<HashSet<BaseAction>>() {}.getType()));
        for (BaseAction action : actions) {
            if (action instanceof FunctionAction) ((FunctionAction) action).setBaseFunction(this);
        }

        pinIdMap.putAll(gson.fromJson(jsonObject.get("pinIdMap"), new TypeToken<HashMap<String, String>>() {}.getType()));

        for (Pin pin : tmpPins) {
            addPin(pin);
        }
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        endFunctionAction = null;
        for (BaseAction action : actions) {
            if (action.getId().equals(startActionId)) {
                outContext = actionContext;
                action.doAction(runnable, this, ((FunctionAction) action).getExecutePin());
                break;
            }
        }

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
            if (endFunctionAction == null) return pin.getValue();
            else {
                for (Pin innerPin : endFunctionAction.getPins()) {
                    if (pin.getId().equals(pinIdMap.get(innerPin.getId()))) {
                        return endFunctionAction.getPinValue(runnable, actionContext, innerPin);
                    }
                }
                throw new RuntimeException("没有对应的插槽");
            }
        } else {
            return super.getPinValue(runnable, actionContext, pin);
        }
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

    public PinObject getInnerPinValue(TaskRunnable runnable, Pin innerPin) {
        String pinId = pinIdMap.get(innerPin.getId());
        Pin pinById = getPinById(pinId);
        return getPinValue(runnable, outContext, pinById);
    }

    public void addInnerPin(Pin innerPin) {
        Pin copy = innerPin.copy(false);
        copy.setDirection(copy.getDirection() == PinDirection.IN ? PinDirection.OUT : PinDirection.IN);
        Pin addPin = super.addPin(copy);
        pinIdMap.put(innerPin.getId(), addPin.getId());
    }

    public void removeInnerPin(Pin innerPin) {
        String remove = pinIdMap.remove(innerPin.getId());
        super.removePin(getPinById(remove));
    }

    public void setInnerPinValue(Pin innerPin) {
        String pinId = pinIdMap.get(innerPin.getId());
        Pin pinById = getPinById(pinId);
        pinById.setValue(innerPin.getValue().copy());
    }

    public void setInnerPinTitle(Pin innerPin) {
        String pinId = pinIdMap.get(innerPin.getId());
        Pin pinById = getPinById(pinId);
        pinById.setTitle(innerPin.getTitle());
    }

    public void setEndFunctionAction(FunctionAction endFunctionAction) {
        this.endFunctionAction = endFunctionAction;
    }

    public boolean isJustCall() {
        return justCall;
    }

    public void setJustCall(boolean justCall) {
        this.justCall = justCall;
    }

    @Override
    public HashSet<BaseAction> getActions() {
        return actions;
    }

    @Override
    public void addAction(BaseAction action) {
        actions.add(action);
    }

    @Override
    public void removeAction(BaseAction action) {
        for (BaseAction baseAction : actions) {
            if (baseAction.getId().equals(action.getId())) {
                actions.remove(baseAction);
                break;
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
    public boolean isReturned() {
        return endFunctionAction != null;
    }


    public enum FUNCTION_TAG {
        START, END;

        public boolean isStart() {
            return this == START;
        }
    }
}
