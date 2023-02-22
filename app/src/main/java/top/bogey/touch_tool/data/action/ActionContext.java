package top.bogey.touch_tool.data.action;

import java.util.ArrayList;
import java.util.HashSet;

public interface ActionContext {
    HashSet<BaseAction> getActions();
    void addAction(BaseAction action);
    void removeAction(BaseAction action);
    BaseAction getActionById(String id);
    ArrayList<BaseAction> getActionsByClass(Class<? extends BaseAction> actionClass);

    boolean isReturned();
}
