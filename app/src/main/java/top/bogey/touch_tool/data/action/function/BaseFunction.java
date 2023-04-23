package top.bogey.touch_tool.data.action.function;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool.data.Task;
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
    private boolean fastEnd = true;

    private transient ActionContext outContext;
    private transient final HashSet<FunctionAction> startFunctions = new HashSet<>();
    private transient FunctionAction startFunction;
    private transient final HashSet<FunctionAction> endFunctions = new HashSet<>();
    private transient FunctionAction endFunction;

    private transient boolean synced = false;
    private transient TaskRunnable runnable;

    public BaseFunction() {
        super(0);
        functionId = UUID.randomUUID().toString();

        startFunction = new FunctionAction(FUNCTION_TAG.START, this);
        startFunction.x = 1;
        startFunction.y = 1;
        startFunctions.add(startFunction);
        actions.add(startFunction);

        // 不是 this.endFunction
        FunctionAction endFunction = new FunctionAction(FUNCTION_TAG.END, this);
        endFunction.x = 1;
        endFunction.y = 21;
        endFunctions.add(endFunction);
        actions.add(endFunction);
    }

    public BaseFunction(JsonObject jsonObject) {
        super(0, jsonObject);
        functionId = GsonUtils.getAsString(jsonObject, "functionId", UUID.randomUUID().toString());
        taskId = GsonUtils.getAsString(jsonObject, "taskId", null);
        justCall = GsonUtils.getAsBoolean(jsonObject, "justCall", false);
        fastEnd = GsonUtils.getAsBoolean(jsonObject, "fastEnd", true);

        actions.addAll(GsonUtils.getAsType(jsonObject, "actions", new TypeToken<HashSet<BaseAction>>() {
        }.getType(), new HashSet<>()));
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

        attrs.putAll(GsonUtils.getAsType(jsonObject, "attrs", new TypeToken<HashMap<String, PinObject>>() {
        }.getType(), new HashMap<>()));

        for (Pin pin : pinsTmp) {
            // 不能直接调用自身的添加
            super.addPin(pin);
            // 自身的输入针脚的值如果为初始值，需要使用开始动作的输出针脚的值
            if (!pin.getDirection().isOut() && pin.getValue().isEmpty()) {
                Pin innerPin = startFunction.getPinByUid(startFunction.getMappingPinUid(pin.getUid()));
                pin.setValue(innerPin.getValue().copy());
            }
        }
    }

    // 同步最新的内容，包括标题，针脚，状态
    public void sync(ActionContext outContext) {
        // 获取最新的方法
        BaseFunction function;
        if (getParent() == null) function = TaskRepository.getInstance().getFunctionById(functionId);
        else function = ((TaskContext) getParent()).getFunctionById(functionId);
        if (function == null) return;
        // 标题
        setTitle(function.getTitle(null));

        // 仅获取状态
        if (function.isJustCall() != justCall) {
            inPin.removeLinks(outContext);
            outPin.removeLinks(outContext);
        }
        setJustCall(function.isJustCall());
        setFastEnd(function.isFastEnd());

        // 外部针脚
        // 先移除自身多出的外部针脚
        for (int i = getPins().size() - 1; i >= 0; i--) {
            Pin pin = getPins().get(i);
            Pin pinByUid = function.getPinByUid(pin.getUid());
            if (pinByUid == null) {
                pin.removeLinks(outContext);
                removePin(outContext, pin);
            }
        }

        // 再同步最新的外部针脚
        for (Pin pin : function.getPins()) {
            Pin pinByUid = getPinByUid(pin.getUid());
            if (pinByUid == null) {
                // 新的方法里有这个针脚，旧的没有，需要在自己这加上
                Pin copy = pin.copy(false);
                // 同步新针脚id，针脚连接需要这个
                copy.setId(pin.getId());
                addPin(copy);
            } else if (!pinByUid.getPinClass().equals(pin.getPinClass())) {
                // 针脚的类型变了，先移除原来的针脚，再重新加回来
                pinByUid.removeLinks(outContext);
                int index = getPins().indexOf(pinByUid);
                removePin(outContext, pinByUid);
                Pin copy = pin.copy(false);
                // 同步新针脚id，针脚连接需要这个
                copy.setId(pin.getId());
                addPin(index, copy);
            } else {
                // 有这个针脚，且针脚值正确，强制同步一下针脚名
                pinByUid.setTitle(pin.getTitle(null));
            }
        }
    }

    // 同步内部信息，这个只需要直接复制就行
    private void syncInner() {
        // 获取最新的方法
        BaseFunction function;
        if (getParent() == null) function = TaskRepository.getInstance().getFunctionById(functionId);
        else function = ((TaskContext) getParent()).getFunctionById(functionId);
        if (function == null) return;

        // 内部属性
        attrs.clear();
        attrs.putAll(GsonUtils.copy(function.getAttrs(), new TypeToken<HashMap<String, PinObject>>() {
        }.getType()));

        // 内部动作
        actions.clear();
        actions.addAll(GsonUtils.copy(function.getActions(), new TypeToken<HashSet<BaseAction>>() {
        }.getType()));

        startFunctions.clear();
        endFunctions.clear();
        for (BaseAction action : actions) {
            if (action instanceof FunctionAction) {
                FunctionAction functionAction = (FunctionAction) action;
                functionAction.setBaseFunction(this);
                if (functionAction.getTag().isStart()) {
                    startFunctions.add(functionAction);
                    // 开始动作是唯一的
                    startFunction = functionAction;
                } else endFunctions.add(functionAction);
            }
        }
    }

    @Override
    public void doAction(TaskRunnable runnable, ActionContext actionContext, Pin pin) {
        endFunction = null;
        this.runnable = runnable;
        outContext = actionContext;

        if (!synced) {
            sync(outContext);
            syncInner();
            synced = true;
        }

        if (pin.getUid().equals(inPin.getUid())) {
            startFunction.doAction(runnable, this, startFunction.getExecutePin());
        } else {
            Pin pinByUid = startFunction.getPinByUid(startFunction.getMappingPinUid(pin.getUid()));
            if (pinByUid != null) {
                startFunction.doAction(runnable, this, pinByUid);
            }
        }

        if (!justCall) {
            // 保底执行
            if (endFunction == null) doNextAction(runnable, actionContext, outPin);
        }
    }

    public void doEndFunction(FunctionAction endFunction, Pin endPin) {
        this.endFunction = endFunction;

        boolean flag = true;
        if (endFunction != null && endPin != null) {
            if (!endPin.getUid().equals(endFunction.getExecutePin().getUid())) {
                String pinUid = endFunction.getPinUidMap().get(endPin.getUid());
                Pin pinByUid = getPinByUid(pinUid);
                if (pinByUid != null) {
                    doNextAction(runnable, outContext, pinByUid);
                    flag = false;
                }
            }
        }
        if (flag) {
            doNextAction(runnable, outContext, outPin);
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
                Pin pinByUid = endFunction.getPinByUid(endFunction.getMappingPinUid(pin.getUid()));
                outContext = actionContext;
                return endFunction.getPinValue(runnable, this, pinByUid);
            }
        } else {
            return super.getPinValue(runnable, actionContext, pin);
        }
    }

    public PinObject getPinValue(TaskRunnable runnable, String pinUid) {
        Pin pin = getPinByUid(pinUid);
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
        String pinUid = functionAction.getPinUidMap().get(innerPin.getUid());
        Pin pin = getPinByUid(pinUid);
        pin.setValue(value);

        for (FunctionAction function : (functionAction.getTag().isStart() ? startFunctions : endFunctions)) {
            function.setPinValue(pinUid, value);
        }
    }

    public void setPinTitle(FunctionAction functionAction, Pin innerPin, String title) {
        String pinUid = functionAction.getPinUidMap().get(innerPin.getUid());
        Pin pin = getPinByUid(pinUid);
        pin.setTitle(title);

        for (FunctionAction function : (functionAction.getTag().isStart() ? startFunctions : endFunctions)) {
            function.setPinTitle(pinUid, title);
        }
    }

    public boolean isJustCall() {
        return justCall;
    }

    public void setJustCall(boolean justCall) {
        this.justCall = justCall;
    }

    public boolean isFastEnd() {
        return fastEnd;
    }

    public void setFastEnd(boolean fastEnd) {
        this.fastEnd = fastEnd;
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
    public PinObject findAttr(String key) {
        PinObject attr = attrs.get(key);
        if (attr != null) return attr;
        else {
            ActionContext parent = getParent();
            if (parent != null) return parent.getAttr(key);
        }
        return null;
    }

    @Override
    public boolean isReturned() {
        return fastEnd && endFunction != null;
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
        // 运行中的话，找运行中的上下文
        ActionContext context = outContext;
        while (context != null) {
            if (context instanceof Task) {
                if (((Task) context).getId().equals(taskId)) return context;
            } else {
                context = context.getParent();
            }
        }
        return TaskRepository.getInstance().getTaskById(taskId);
    }

    public enum FUNCTION_TAG {
        START, END;

        public boolean isStart() {
            return this == START;
        }
    }
}
