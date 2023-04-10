package top.bogey.touch_tool.data.action.function;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool.data.TaskContext;
import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.data.TaskRunnable;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.NormalAction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.GsonUtils;

/* BaseFunction只是一个包装，所有针脚都是重定向到startAction和endAction去执行。
 */
public class BaseFunction extends NormalAction implements ActionContext {
    private String functionId;
    private String taskId;

    private final HashSet<BaseAction> actions = new HashSet<>();
    private final HashMap<String, PinObject> attrs = new HashMap<>();
    private boolean justCall = false;

    private transient ActionContext outContext;
    private transient final HashSet<FunctionAction> startFunctions = new HashSet<>();
    private transient FunctionAction startFunction;
    private transient final HashSet<FunctionAction> endFunctions = new HashSet<>();
    private transient FunctionAction endFunction;

    public BaseFunction() {
        super(0);
        functionId = UUID.randomUUID().toString();

        startFunction = new FunctionAction(FUNCTION_TAG.START, this);
        startFunctions.add(startFunction);
        actions.add(startFunction);

        // 不是 this.endFunction
        FunctionAction endFunction = new FunctionAction(FUNCTION_TAG.END, this);
        endFunctions.add(endFunction);
        actions.add(endFunction);
    }

    public BaseFunction(JsonObject jsonObject) {
        super(0, jsonObject);
        functionId = GsonUtils.getAsString(jsonObject, "functionId", UUID.randomUUID().toString());
        taskId = GsonUtils.getAsString(jsonObject, "taskId", null);
        justCall = GsonUtils.getAsBoolean(jsonObject, "justCall", false);

        actions.addAll(GsonUtils.getAsType(jsonObject, "actions", new TypeToken<HashSet<BaseAction>>() {}.getType(), new HashSet<>()));
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

        attrs.putAll(GsonUtils.getAsType(jsonObject, "attrs", new TypeToken<HashMap<String, PinObject>>() {}.getType(), new HashMap<>()));

        for (Pin pin : pinsTmp) {
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
        BaseFunction copy = GsonUtils.copy(this, BaseFunction.class);
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

    // 同步最新的内容，包括标题，针脚，状态，内部动作， 内部属性
    public void sync(ActionContext outContext) {
        // 获取最新的方法
        BaseFunction function;
        if (getParent() == null) function = TaskRepository.getInstance().getFunctionById(functionId);
        else function = ((TaskContext) getParent()).getFunctionById(functionId);
        // 标题
        setTitle(function.getTitle(null));

        // 仅获取状态
        if (function.isJustCall() != justCall) {
            inPin.removeLinks(outContext);
            outPin.removeLinks(outContext);
        }
        setJustCall(function.isJustCall());

        // 针脚
        // 先移除自身多出的针脚
        ArrayList<Pin> pins = getPins();
        for (int i = pins.size() - 1; i >= 0; i--) {
            Pin pin = pins.get(i);
            Pin pinById = function.getPinByUid(pin.getUid());
            if (pinById == null) {
                pin.removeLinks(outContext);
                removePin(outContext, pin);
            }
        }

        // 再同步最新的针脚
        for (Pin pin : function.getPins()) {
            Pin pinById = getPinByUid(pin.getUid());
            if (pinById == null) {
                // 新的方法里有这个针脚，旧的没有，需要在自己这加上
                Pin copy = pin.copy(false);
                copy.setId(pin.getId());
                addPin(copy);
            } else if (!pinById.getPinClass().equals(pin.getPinClass())) {
                // 针脚的类型变了，先移除原来的针脚，再重新加回来
                pinById.removeLinks(outContext);
                int index = getPins().indexOf(pinById);
                removePin(outContext, pinById);
                Pin copy = pin.copy(false);
                copy.setId(pin.getId());
                addPin(index, copy);
            } else {
                // 同步一下针脚名
                pinById.setTitle(pin.getTitle(null));
            }
        }

        // 内部属性
        // 先移除多出来的属性
        for (String key : attrs.keySet()) {
            PinObject attr = function.getAttr(key);
            if (attr == null) {
                attrs.remove(key);
            }
        }

        // 再同步最新的属性
        function.getAttrs().forEach((key, value) -> {
            PinObject attr = getAttr(key);
            if (attr == null) {
                // 自己没有，同步下来
                addAttr(key, value.copy());
            } else if (!attr.getClass().equals(value.getClass())) {
                // 变量类型不一致，同步下来
                addAttr(key, value.copy());
            }
        });

        // 内部动作
        // 先移除多出来的动作
        actions.removeIf(action -> {
            BaseAction actionById = function.getActionById(action.getId());
            return actionById == null;
        });

        // 再同步最新的动作
        for (BaseAction action : function.getActions()) {
            BaseAction actionById = getActionById(action.getId());
            if (actionById == null) {
                // 自己没有，同步下
                BaseAction copy = GsonUtils.copy(action, BaseAction.class);
                addAction(copy);
            } else {
                // 自己有，但可能针脚连接有变化，同步下连接
                removeAction(actionById);
                BaseAction copy = GsonUtils.copy(action, BaseAction.class);
                if (copy instanceof FunctionAction) {
                    // 开始或结束动作需要保持针脚映射表
                    HashMap<String, String> pinIdMap = ((FunctionAction) copy).getPinIdMap();
                    pinIdMap.clear();
                    pinIdMap.putAll(((FunctionAction) actionById).getPinIdMap());
                }
                addAction(copy);
            }
        }
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        endFunction = null;
        outContext = actionContext;
        sync(outContext);
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
    public Pin removePin(ActionContext context, Pin pin) {
        Pin removePin = super.removePin(context, pin);
        if (removePin == null) return null;
        for (FunctionAction function : (pin.getDirection().isOut() ? endFunctions : startFunctions)) {
            function.removePin(this, pin);
        }
        return removePin;
    }

    public void setPinValue(FunctionAction functionAction, Pin innerPin, PinObject value) {
        String pinId = functionAction.getPinIdMap().get(innerPin.getId());
        Pin pin = getPinById(pinId);
        pin.setValue(value);

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

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    @Override
    public HashSet<BaseAction> getActions() {
        return actions;
    }

    @Override
    public void addAction(BaseAction action) {
        actions.add(action);

        // 更新开始结束动作
        if (action instanceof FunctionAction) {
            FunctionAction functionAction = (FunctionAction) action;
            if (functionAction.getTag().isStart()) {
                startFunctions.add(functionAction);
                startFunction = functionAction;
            } else {
                endFunctions.add(functionAction);
            }
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

        // 更新开始结束动作
        if (action instanceof FunctionAction) {
            FunctionAction functionAction = (FunctionAction) action;
            if (functionAction.getTag().isStart()) {
                for (FunctionAction function : startFunctions) {
                    if (function.getId().equals(functionAction.getId())) {
                        startFunctions.remove(function);
                        break;
                    }
                }
                if (startFunction != null && startFunction.getId().equals(functionAction.getId())) {
                    startFunction = null;
                }
            } else {
                for (FunctionAction function : endFunctions) {
                    if (function.getId().equals(functionAction.getId())) {
                        endFunctions.remove(function);
                        break;
                    }
                }
                if (endFunction != null && endFunction.getId().equals(functionAction.getId())) {
                    endFunction = null;
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

    @Override
    public void save() {
        ActionContext parent = getParent();
        if (parent != null) parent.save();
        else TaskRepository.getInstance().saveFunction(this);
    }

    @Override
    public ActionContext getParent() {
        if (taskId == null) return null;
        return TaskRepository.getInstance().getTaskById(taskId);
    }

    public enum FUNCTION_TAG {
        START, END;

        public boolean isStart() {
            return this == START;
        }
    }
}
