package top.bogey.touch_tool.data.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import top.bogey.touch_tool.data.pin.object.PinObject;

public interface ActionContext {
    HashSet<BaseAction> getActions();

    void addAction(BaseAction action);

    void removeAction(BaseAction action);

    BaseAction getActionById(String id);

    ArrayList<BaseAction> getActionsByClass(Class<? extends BaseAction> actionClass);

    HashMap<String, PinObject> getAttrs();

    void addAttr(String key, PinObject value);

    void removeAttr(String key);

    PinObject getAttr(String key);

    PinObject findAttr(String key);

    boolean isReturned();

    void save();

    ActionContext getParent();
}
