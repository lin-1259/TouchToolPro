package top.bogey.touch_tool.data.action;

import java.util.HashSet;

public interface ActionContext {
    HashSet<BaseAction> getActions();
    boolean isReturned();
}
