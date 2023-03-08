package top.bogey.touch_tool.data;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import top.bogey.touch_tool.R;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.action.CaptureServiceAction;
import top.bogey.touch_tool.data.action.start.StartAction;
import top.bogey.touch_tool.data.action.state.ColorStateAction;
import top.bogey.touch_tool.data.action.state.ImageStateAction;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class Task implements ActionContext {
    private String id;
    private final HashSet<BaseAction> actions = new HashSet<>();
    private final HashMap<String, PinObject> attrs = new HashMap<>();

    private final long createTime;
    private String tag;

    private String title;

    public Task() {
        id = UUID.randomUUID().toString();
        createTime = System.currentTimeMillis();
    }

    public Task copy() {
        Gson gson = TaskRepository.getInstance().getGson();
        String json = gson.toJson(this);
        return gson.fromJson(json, Task.class);
    }

    public ArrayList<StartAction> getStartActions(Class<? extends StartAction> startActionClass) {
        ArrayList<StartAction> startActions = new ArrayList<>();
        for (BaseAction action : actions) {
            if (startActionClass.isInstance(action)) {
                startActions.add((StartAction) action);
            }
        }
        return startActions;
    }

    @Override
    public void addAction(BaseAction action) {
        actions.add(action);
    }

    @Override
    public void removeAction(BaseAction action) {
        for (BaseAction baseAction : actions) {
            if (baseAction.getId().equals(action.getId())) {
                actions.remove(baseAction);
                break;
            }
        }
    }

    @Override
    public BaseAction getActionById(String id) {
        for (BaseAction action : actions) {
            if (action.getId().equals(id)) return action;
        }
        return null;
    }

    @Override
    public ArrayList<BaseAction> getActionsByClass(Class<? extends BaseAction> actionClass) {
        ArrayList<BaseAction> actions = new ArrayList<>();
        for (BaseAction action : this.actions) {
            if (actionClass.isInstance(action)) {
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    public HashMap<String, PinObject> getAttrs() {
        return attrs;
    }

    @Override
    public void addAttr(String key, PinObject value) {
        attrs.put(key, value);
    }

    @Override
    public void removeAttr(String key) {
        attrs.remove(key);
    }

    @Override
    public PinObject getAttr(String key) {
        return attrs.get(key);
    }

    public String getTaskDes(Context context) {
        StringBuilder builder = new StringBuilder();
        for (StartAction startAction : getStartActions(StartAction.class)) {
            String title = startAction.getTitle();
            if (title == null) continue;
            builder.append(title);
            builder.append("(");
            if (startAction.isEnable()) {
                builder.append(context.getString(R.string.action_start_subtitle_enable_true));
            } else {
                builder.append(context.getString(R.string.action_start_subtitle_enable_false));
            }
            builder.append(")");
            builder.append("\n");
        }
        return builder.toString().trim();
    }

    public boolean needCaptureService() {
        ArrayList<BaseAction> captureActions = getActionsByClass(CaptureServiceAction.class);
        if (captureActions.size() > 0) return false;
        ArrayList<BaseAction> imageActions = getActionsByClass(ImageStateAction.class);
        ArrayList<BaseAction> colorActions = getActionsByClass(ColorStateAction.class);
        return imageActions.size() + colorActions.size() > 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) id = UUID.randomUUID().toString();
        this.id = id;
    }

    @Override
    public HashSet<BaseAction> getActions() {
        return actions;
    }

    @Override
    public boolean isReturned() {
        return false;
    }

    @Override
    public void save() {
        TaskRepository.getInstance().saveTask(this);
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
