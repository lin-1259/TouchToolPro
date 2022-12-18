package top.bogey.touch_tool.data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import top.bogey.touch_tool.data.action.BaseAction;

public class Task {
    private final String id;
    private final Set<BaseAction> actions = new HashSet<>();

    public Task() {
        id = UUID.randomUUID().toString();
    }

    public boolean matchActionTag(String tag) {
        for (BaseAction action : actions) {
            boolean result = action.matchActionTag(tag);
            if (result) return true;
        }
        return false;
    }

    public BaseAction getActionById(String id) {
        for (BaseAction action : actions) {
            if (action.getId().equals(id)) return action;
        }
        return null;
    }

    public void addAction(BaseAction action) {
        actions.add(action);
    }

    public Set<BaseAction> getActions() {
        return actions;
    }
}
