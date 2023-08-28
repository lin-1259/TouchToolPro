package top.bogey.touch_tool_pro.bean.action.function;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import top.bogey.touch_tool_pro.R;
import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.action.ActionCheckResult;
import top.bogey.touch_tool_pro.bean.action.ActionType;
import top.bogey.touch_tool_pro.bean.base.SaveRepository;
import top.bogey.touch_tool_pro.bean.function.Function;
import top.bogey.touch_tool_pro.bean.function.FunctionContext;
import top.bogey.touch_tool_pro.bean.pin.Pin;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.task.TaskRunnable;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public class FunctionReferenceAction extends Action {
    private String parentId;
    private final String functionId;

    private transient Function function;
    private transient FunctionContext outContext;
    private transient Function executeFunction;

    private transient boolean synced = false;

    public FunctionReferenceAction(Function function) {
        super(ActionType.CUSTOM);
        parentId = function.getParentId();
        functionId = function.getId();
    }

    public FunctionReferenceAction(JsonObject jsonObject) {
        super(jsonObject);
        parentId = GsonUtils.getAsString(jsonObject, "parentId", null);
        functionId = GsonUtils.getAsString(jsonObject, "functionId", null);
        tmpPins.forEach(this::addPin);
        tmpPins.clear();
    }

    @Override
    public String getTitle() {
        if (function == null) return super.getTitle();
        return function.getTitle();
    }

    @Override
    public ActionCheckResult check(FunctionContext context) {
        function = SaveRepository.getInstance().getFunction(parentId, functionId);
        if (function == null) {
            return new ActionCheckResult(ActionCheckResult.ActionResultType.ERROR, R.string.error_function_reference_action_tips);
        }
        return super.check(context);
    }

    @Override
    public void execute(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (!synced) sync(context);
        if (function == null) return;

        outContext = context;
        executeFunction = function.newContext(this);
        function.execute(runnable, executeFunction, pin);
    }

    @Override
    public void executeNext(TaskRunnable runnable, FunctionContext context, Pin pin) {
        super.executeNext(runnable, outContext, getPinByUid(pin.getUid()));
    }

    @Override
    public void calculate(TaskRunnable runnable, FunctionContext context, Pin pin) {
        outContext = context;
        if (executeFunction == null) {
            executeFunction = function.newContext(this);
        }
        function.calculate(runnable, executeFunction, pin);
    }

    @Override
    public PinObject getPinValue(TaskRunnable runnable, FunctionContext context, Pin pin) {
        if (pin.isOut() && !isError(context)) {
            calculate(runnable, context, pin);
            FunctionEndAction endAction = executeFunction.getEndAction();
            if (endAction == null) return pin.getValue();
            Pin pinByUid = endAction.getPinByUid(pin.getUid());
            return endAction.getPinValue(runnable, executeFunction, pinByUid);
        }
        return super.getPinValue(runnable, outContext, pin);
    }

    public void sync(FunctionContext context) {
        function = SaveRepository.getInstance().getFunction(parentId, functionId);
        if (function == null) return;

        ArrayList<Pin> pins = new ArrayList<>(getPins());
        ArrayList<Pin> functionPins = function.getPins();

        // 移除多余的针脚
        for (Pin pin : pins) {
            boolean flag = true;
            for (Pin functionPin : functionPins) {
                if (pin.getUid().equals(functionPin.getUid())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                removePin(pin, context);
            }
        }

        // 再同步最新的针脚
        for (Pin functionPin : functionPins) {
            Pin pin = getPinByUid(functionPin.getUid());
            // 如果之前没有，直接复制一份加上
            if (pin == null) {
                addPin((Pin) functionPin.copy());
                continue;
            }
            // 如果之前有，但值不对，断开所有连接，并复制值
            if (!functionPin.isValueMatched(pin)) {
                pin.cleanLinks(context);
                pin.setValue(functionPin.getValue().copy());
            }
            pin.setTitle(functionPin.getTitle());
        }
        synced = true;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getFunctionId() {
        return functionId;
    }
}
