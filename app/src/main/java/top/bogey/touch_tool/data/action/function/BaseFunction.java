package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.HashSet;

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
    private final HashSet<BaseAction> actions = new HashSet<>();
    private final HashMap<String, String> pinIdMap = new HashMap<>();
    private final String startActionId;

    private boolean justCall = false;

    private transient ActionContext outContext;
    private transient FunctionAction endFunctionAction;

    public BaseFunction(Context context) {
        super(context, 0);
        FunctionAction startAction = new FunctionAction(context, FUNCTION_TAG.START);
        startActionId = startAction.getId();
        actions.add(startAction);
        actions.add(new FunctionAction(context, FUNCTION_TAG.END));
    }

    public BaseFunction(JsonObject jsonObject) {
        super(jsonObject);

        startActionId = jsonObject.get("startActionId").getAsString();
        justCall = jsonObject.get("justCall").getAsBoolean();

        Gson gson = TaskRepository.getInstance().getGson();
        actions.addAll(gson.fromJson(jsonObject.get("actions"), new TypeToken<HashSet<BaseAction>>() {}.getType()));
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
                action.doAction(runnable, this, ((FunctionAction) action).getInPin());
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

    public PinObject getPinValue(TaskRunnable runnable, Pin innerPin) {
        String pinId = pinIdMap.get(innerPin.getId());
        Pin pinById = getPinById(pinId);
        return getPinValue(runnable, outContext, pinById);
    }

    public void setEndFunctionAction(FunctionAction endFunctionAction) {
        this.endFunctionAction = endFunctionAction;
    }

    @Override
    public HashSet<BaseAction> getActions() {
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
