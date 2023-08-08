package top.bogey.touch_tool_pro.bean.base;

import top.bogey.touch_tool_pro.bean.function.Function;

public interface FunctionSaveChangedListener {
    void onCreated(Function value);

    void onChanged(Function value);

    void onRemoved(Function value);
}
