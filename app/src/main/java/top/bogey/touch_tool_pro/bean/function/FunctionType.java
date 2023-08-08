package top.bogey.touch_tool_pro.bean.function;

import top.bogey.touch_tool_pro.bean.task.Task;

public enum FunctionType {
    FUNCTION,
    TASK;

    public Class<? extends FunctionContext> getFunctionClass() {
        return switch (this) {
            case FUNCTION -> Function.class;
            case TASK -> Task.class;
        };
    }
}
