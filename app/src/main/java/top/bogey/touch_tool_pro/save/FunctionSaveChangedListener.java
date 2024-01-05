package top.bogey.touch_tool_pro.save;

import top.bogey.touch_tool_pro.bean.function.Function;

public interface FunctionSaveChangedListener {
    void onCreated(Function value);

    void onChanged(Function value);

    void onRemoved(Function value);
}
