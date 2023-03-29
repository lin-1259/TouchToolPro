package top.bogey.touch_tool.data;

import java.util.ArrayList;

import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.function.BaseFunction;

public interface TaskContext extends ActionContext {
    ArrayList<BaseFunction> getFunctions();
    void addFunction(BaseFunction function);
    void removeFunction(String functionId);
    BaseFunction getFunctionById(String functionId);
}
