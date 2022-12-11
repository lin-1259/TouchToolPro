package top.bogey.touch_tool.data.action;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import top.bogey.touch_tool.data.TaskHelper;

public class BaseAction {
    protected final String id;
    protected final String[] tags;

    protected boolean enable = true;

    private BaseAction inAction;
    private final List<BaseAction> inActions = new ArrayList<>();

    private BaseAction outAction;
    private final List<BaseAction> outActions = new ArrayList<>();

    public BaseAction(String[] tags) {
        id = UUID.randomUUID().toString();
        this.tags = tags;
    }

    public BaseAction(String id, String[] tags) {
        this.id = id;
        this.tags = tags;
    }

    public boolean doAction(TaskHelper taskHelper) {
        return true;
    }

    public boolean checkState(TaskHelper taskHelper) {
        return true;
    }

    public boolean isValid() {
        return true;
    }

    protected void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public BaseAction getInAction() {
        return inAction;
    }

    public void setInAction(BaseAction inAction) {
        this.inAction = inAction;
    }

    public List<BaseAction> getInActions() {
        return inActions;
    }

    public void addInAction(BaseAction inAction) {
        inActions.add(inAction);
    }

    public BaseAction getOutAction() {
        return outAction;
    }

    public void setOutAction(BaseAction outAction) {
        this.outAction = outAction;
    }

    public List<BaseAction> getOutActions() {
        return outActions;
    }

    public void addOutAction(BaseAction outAction) {
        inActions.add(outAction);
    }
}
