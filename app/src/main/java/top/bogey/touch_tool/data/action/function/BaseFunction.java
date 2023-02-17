package top.bogey.touch_tool.data.action.function;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

/* BaseFunction只是一个包装，所有针脚都是重定向到startAction和endAction去执行。
 */
public class BaseFunction extends BaseAction implements ActionContext {
    private final HashSet<BaseAction> actions = new HashSet<>();
    private String functionName;
    private final HashMap<String, String> pinIdMap = new HashMap<>();
    private final FunctionAction startAction;
    private final FunctionAction endAction;

    private transient ActionContext outContext;

    public BaseFunction(Context context) {
        super(context);
        startAction = new FunctionAction(context, FUNCTION_TAG.START);
        startAction.setBaseFunction(this);

        endAction = new FunctionAction(context, FUNCTION_TAG.END);
        endAction.setBaseFunction(this);
    }

    public BaseFunction(JsonObject jsonObject) {
        super(jsonObject);
        Gson gson = TaskRepository.getInstance().getGson();
        actions.addAll(gson.fromJson(jsonObject.get("actions"), new TypeToken<HashSet<BaseAction>>() {}.getType()));

        startAction = gson.fromJson(jsonObject.get("startAction"), FunctionAction.class);
        startAction.setBaseFunction(this);

        endAction = gson.fromJson(jsonObject.get("endAction"), FunctionAction.class);
        endAction.setBaseFunction(this);

        pinIdMap.putAll(gson.fromJson(jsonObject.get("pinIdMap"), new TypeToken<HashMap<String, String>>() {}.getType()));

        for (Pin pin : tmpPins) {
            addPin(pin);
        }
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        String pinId = pinIdMap.get(pin.getId());
        Pin startPin = startAction.getPinById(pinId);
        outContext = actionContext;
        startAction.doAction(runnable, this, startPin);
    }

    @Override
    protected void doNextAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        Pin endPin = getPinByMap(pin);
        if (endPin == null) throw new RuntimeException("未找到针脚");
        super.doNextAction(runnable, outContext, endPin);
    }

    @Override
    protected PinObject getPinValue(ActionContext actionContext, Pin pin) {
        String pinId = pinIdMap.get(pin.getId());
        Pin valuePin = endAction.getPinById(pinId);
        if (valuePin == null) {
            valuePin = getPinByMap(pin);
            if (valuePin == null) throw new RuntimeException("未找到针脚");
            return super.getPinValue(outContext, valuePin);
        } else {
            outContext = actionContext;
            return endAction.getPinValue(this, valuePin);
        }
    }

    private Pin getPinByMap(Pin pin) {
        for (Map.Entry<String, String> entry : pinIdMap.entrySet()) {
            if (entry.getValue().equals(pin.getId())) {
                return getPinById(entry.getKey());
            }
        }
        return null;
    }

    @Override
    public HashSet<BaseAction> getActions() {
        return actions;
    }

    public enum FUNCTION_TAG {
        START, END;

        public boolean isStart() {
            return this == START;
        }
    }
}
