package top.bogey.touch_tool_pro.bean.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import top.bogey.touch_tool_pro.bean.action.Action;
import top.bogey.touch_tool_pro.bean.base.IdentityInfo;
import top.bogey.touch_tool_pro.bean.pin.pins.PinObject;
import top.bogey.touch_tool_pro.bean.pin.pins.PinValue;
import top.bogey.touch_tool_pro.utils.GsonUtils;

public abstract class FunctionContext extends IdentityInfo {
    private final FunctionType type;
    // 配置的动作
    private final HashSet<Action> actions = new HashSet<>();
    // 配置的变量
    private final HashMap<String, PinValue> vars = new HashMap<>();

    private HashSet<String> tags;

    public FunctionContext(FunctionType type) {
        super();
        this.type = type;
    }

    public FunctionContext(JsonObject jsonObject) {
        super(jsonObject);
        type = GsonUtils.getAsObject(jsonObject, "type", FunctionType.class, FunctionType.FUNCTION);
        actions.addAll(GsonUtils.getAsObject(jsonObject, "actions", TypeToken.getParameterized(HashSet.class, Action.class).getType(), new HashSet<>()));
        vars.putAll(GsonUtils.getAsObject(jsonObject, "vars", TypeToken.getParameterized(HashMap.class, String.class, PinObject.class).getType(), new HashMap<>()));
        tags = GsonUtils.getAsObject(jsonObject, "tags", TypeToken.getParameterized(HashSet.class, String.class).getType(), new HashSet<>());
    }

    public HashSet<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        getActions().add(action);
    }

    public void removeAction(Action action) {
        getActions().remove(action);
    }

    public Action getActionById(String id) {
        for (Action action : getActions()) {
            if (action.getId().equals(id)) return action;
        }
        return null;
    }

    public ArrayList<Action> getActionsByClass(Class<? extends Action> actionClass) {
        ArrayList<Action> actions = new ArrayList<>();
        for (Action action : getActions()) {
            if (actionClass.isInstance(action)) actions.add(action);
        }
        return actions;
    }

    public HashMap<String, PinValue> getVars() {
        return vars;
    }

    public void addVar(String key, PinValue value) {
        vars.put(key, value);
    }

    public PinValue removeVar(String key) {
        return vars.remove(key);
    }

    public PinValue getVar(String key) {
        return vars.get(key);
    }

    public PinValue findVar(String key) {
        PinValue var = getVar(key);
        if (var != null) return var;
        FunctionContext parent = getParent();
        if (parent != null) return parent.getVar(key);
        return null;
    }

    public FunctionContext findVarParent(String key) {
        PinValue var = getVar(key);
        if (var != null) return this;
        FunctionContext parent = getParent();
        if (parent != null) return parent.findVarParent(key);
        return null;
    }

    public void setVarOnParent(String key, PinValue value) {
        PinValue var = getVar(key);
        if (var != null) {
            addVar(key, value);
        } else {
            FunctionContext parent = getParent();
            if (parent != null) {
                parent.addVar(key, value);
            }
        }
    }

    public HashSet<String> getTags() {
        return tags;
    }

    public String getTagString() {
        if (tags == null || tags.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (String tag : tags) {
            builder.append(tag).append(",");
        }
        if (builder.length() == 0) return "";
        return builder.substring(0, builder.length() - 1);
    }

    public void addTag(String tag) {
        if (tags == null) tags = new HashSet<>();
        tags.add(tag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
        if (tags.size() == 0) {
            tags = null;
        }
    }

    public void removeAllTags() {
        tags = null;
    }

    public boolean check(ArrayList<Action> errors) {
        boolean flag = true;
        for (Action action : getActions()) {
            if (action.check(this)) continue;
            flag = false;
            if (errors != null) errors.add(action);
        }
        return flag;
    }

    public abstract void save();

    public abstract FunctionContext getParent();

    public boolean isEnd() {
        return false;
    }

    public FunctionType getType() {
        return type;
    }

    public static class FunctionContextDeserializer implements JsonDeserializer<FunctionContext> {
        @Override
        public FunctionContext deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            FunctionType type = GsonUtils.getAsObject(jsonObject, "type", FunctionType.class, FunctionType.FUNCTION);
            Class<? extends FunctionContext> functionClass = type.getFunctionClass();
            try {
                Constructor<? extends FunctionContext> constructor = functionClass.getConstructor(JsonObject.class);
                return constructor.newInstance(jsonObject);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
