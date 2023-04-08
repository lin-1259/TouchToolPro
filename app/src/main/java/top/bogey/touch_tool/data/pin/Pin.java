package top.bogey.touch_tool.data.pin;

import android.content.Context;

import androidx.annotation.StringRes;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.object.PinExecute;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.GsonUtils;

public class Pin {
    private String uid;
    private String id;
    private String title;
    private PinObject value;

    private PinDirection direction;
    private PinSubType subType;

    private boolean removeAble;
    private final HashMap<String, String> links = new HashMap<>();

    private transient String actionId;
    private transient int titleId;
    private transient final HashSet<LinkListener> listeners = new HashSet<>();

    public Pin(PinObject value) {
        this(value, 0, PinDirection.IN, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, @StringRes int titleId) {
        this(value, titleId, PinDirection.IN, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, PinDirection direction) {
        this(value, 0, direction, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, @StringRes int titleId, PinDirection direction) {
        this(value, titleId, direction, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title, PinDirection direction) {
        this(value, 0, direction, PinSubType.NORMAL, false);
        this.title = title;
    }

    public Pin(PinObject value, @StringRes int titleId, PinSubType subType) {
        this(value, titleId, PinDirection.IN, subType, false);
    }

    public Pin(PinObject value, @StringRes int titleId, PinDirection direction, PinSubType subType, boolean removeAble) {
        if (value == null) throw new RuntimeException("针脚的值为空");
        uid = UUID.randomUUID().toString();

        this.id = UUID.randomUUID().toString();
        this.titleId = titleId;

        this.value = value;

        this.direction = direction;
        this.subType = subType;

        this.removeAble = removeAble;
    }

    public Pin(JsonObject jsonObject) {
        if (jsonObject == null) return;
        uid = GsonUtils.getAsString(jsonObject, "uid", UUID.randomUUID().toString());

        id = GsonUtils.getAsString(jsonObject, "id", UUID.randomUUID().toString());
        title = GsonUtils.getAsString(jsonObject, "title", null);

        direction = PinDirection.valueOf(GsonUtils.getAsString(jsonObject, "direction", PinDirection.IN.toString()));
        subType = PinSubType.valueOf(GsonUtils.getAsString(jsonObject, "subType", PinSubType.NORMAL.toString()));

        removeAble = GsonUtils.getAsBoolean(jsonObject, "removeAble", false);
        links.putAll(GsonUtils.getAsType(jsonObject, "links", new TypeToken<HashMap<String, String>>() {}.getType(), new HashMap<>()));
        value = GsonUtils.getAsClass(jsonObject, "value", PinObject.class, null);
    }

    public Pin copy(boolean removeAble) {
        Pin copy = GsonUtils.copy(this, Pin.class);
        copy.id = UUID.randomUUID().toString();
        copy.removeAble = removeAble;
        return copy;
    }

    public Pin getLinkedPin(ActionContext actionContext) {
        for (Map.Entry<String, String> entry : links.entrySet()) {
            BaseAction action = null;
            for (BaseAction baseAction : actionContext.getActions()) {
                if (baseAction.getId().equals(entry.getValue())) {
                    action = baseAction;
                    break;
                }
            }
            if (action == null) continue;
            Pin pin = action.getPinById(entry.getKey());
            if (pin == null) continue;
            return pin;
        }
        return null;
    }

    public BaseAction getOwner(ActionContext actionContext) {
        for (BaseAction action : actionContext.getActions()) {
            if (action.getId().equals(actionId)) return action;
        }
        return null;
    }

    public boolean addLink(ActionContext context, Pin pin) {
        // 单针脚，需要先移除之前的连接
        if (isSingle()) {
            removeLinks(context);
        }
        links.put(pin.getId(), pin.getActionId());
        if (listeners != null) listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onAdded(pin));
        return true;
    }

    public void removeLink(Pin pin) {
        if (links.remove(pin.getId()) != null) {
            if (listeners != null) listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onRemoved(pin));
        }
    }

    public boolean addLinks(ActionContext context, HashMap<String, String> links) {
        boolean flag = false;
        for (Map.Entry<String, String> entry : links.entrySet()) {
            BaseAction action = context.getActionById(entry.getValue());
            if (action == null) continue;
            Pin pinById = action.getPinById(entry.getKey());
            // 插槽不匹配不能连
            if (!getPinClass().isAssignableFrom(pinById.getPinClass())) continue;

            // 方向相同不能连
            if (getDirection().equals(pinById.getDirection())) continue;

            // 自己不能首尾连
            if (getActionId().equals(pinById.getActionId())) continue;

            // 两边都连上了才行，有一个没连上都要回退
            if (!(pinById.addLink(context, this) && addLink(context, pinById))) {
                pinById.removeLink(this);
                removeLink(pinById);
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public void removeLinks(ActionContext context) {
        HashMap<String, String> map = new HashMap<>(links);
        map.forEach((pinId, actionId) -> {
            BaseAction action = context.getActionById(actionId);
            if (action == null) return;
            Pin pinById = action.getPinById(pinId);
            if (pinById == null) return;
            pinById.removeLink(this);
            links.remove(pinId);
            if (listeners != null) listeners.stream().filter(Objects::nonNull).forEach(listener -> listener.onRemoved(pinById));
        });
        links.clear();
    }

    public void cleanLinks() {
        links.clear();
    }

    public Class<?> getPinClass() {
        return value.getClass();
    }

    public boolean isSingle() {
        if (value instanceof PinExecute) return direction.isOut();
        else return !direction.isOut();
    }

    public boolean isVertical() {
        return value instanceof PinExecute;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle(Context context) {
        if (titleId == 0 || context == null) return title;
        else return context.getString(titleId);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PinObject getValue() {
        return value;
    }

    public void setValue(PinObject value) {
        if (value == null) return;

        // 值类型变了，需要断开连接
        if (!value.getClass().equals(getPinClass())) {
            if (listeners != null) listeners.stream().filter(Objects::nonNull).forEach(LinkListener::onChanged);
        }
        this.value = value;
    }

    public PinDirection getDirection() {
        return direction;
    }

    public void setDirection(PinDirection direction) {
        this.direction = direction;
    }

    public PinSubType getSubType() {
        return subType;
    }

    public boolean isRemoveAble() {
        return removeAble;
    }

    public HashMap<String, String> getLinks() {
        return new HashMap<>(links);
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public void addListener(LinkListener listener) {
        listeners.add(listener);
    }

    public interface LinkListener {
        // 连线了
        void onAdded(Pin pin);

        // 断开连线了
        void onRemoved(Pin pin);

        // 针脚值变更了
        void onChanged();
    }
}
