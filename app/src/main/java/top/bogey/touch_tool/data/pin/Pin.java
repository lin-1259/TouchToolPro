package top.bogey.touch_tool.data.pin;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.GsonUtils;

public class Pin {
    private String id;
    private String title;
    private PinObject value;

    private PinDirection direction;
    private PinSlotType slotType;
    private PinSubType subType;

    private boolean removeAble;
    private final HashMap<String, String> links = new HashMap<>();

    private transient String actionId;
    private transient LinkListener listener;

    public Pin(PinObject value) {
        this(value, null, PinDirection.IN, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title) {
        this(value, title, PinDirection.IN, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, PinSlotType slotType) {
        this(value, null, PinDirection.IN, slotType, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, PinDirection direction) {
        this(value, null, direction, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title, PinDirection direction) {
        this(value, title, direction, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title, PinSubType subType) {
        this(value, title, PinDirection.IN, PinSlotType.SINGLE, subType, false);
    }

    public Pin(PinObject value, String title, PinSlotType slotType) {
        this(value, title, PinDirection.IN, slotType, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, PinDirection direction, PinSlotType slotType) {
        this(value, null, direction, slotType, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title, PinDirection direction, PinSlotType slotType) {
        this(value, title, direction, slotType, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title, PinDirection direction, PinSlotType slotType, PinSubType subType, boolean removeAble) {
        this.id = UUID.randomUUID().toString();
        this.title = title;

        this.value = value;

        this.direction = direction;
        this.slotType = slotType;
        this.subType = subType;

        this.removeAble = removeAble;
    }

    public Pin(JsonObject jsonObject) {
        if (jsonObject == null) return;
        id = GsonUtils.getAsString(jsonObject, "id", UUID.randomUUID().toString());
        title = GsonUtils.getAsString(jsonObject, "title", null);

        direction = PinDirection.valueOf(GsonUtils.getAsString(jsonObject, "direction", null));
        slotType = PinSlotType.valueOf(GsonUtils.getAsString(jsonObject, "slotType", null));
        subType = PinSubType.valueOf(GsonUtils.getAsString(jsonObject, "subType", null));

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
        // 不能连的直接返回
        if (slotType == PinSlotType.EMPTY) return false;
        // 单针脚，需要先移除之前的连接
        if (slotType == PinSlotType.SINGLE) {
            removeLinks(context);
        }
        links.put(pin.getId(), pin.getActionId());
        if (listener != null) listener.onAdded(pin);
        return true;
    }

    public void removeLink(Pin pin) {
        if (links.remove(pin.getId()) != null) {
            if (listener != null) listener.onRemoved(pin);
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
            if (listener != null) listener.onRemoved(pinById);
        });
        links.clear();
    }

    public void cleanLinks() {
        links.clear();
    }

    public Class<?> getPinClass() {
        if (value == null) throw new RuntimeException("针脚的值为空");
        return value.getClass();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PinObject getValue() {
        if (value == null) throw new RuntimeException("针脚的值为空");
        return value;
    }

    public void setValue(PinObject value) {
        if (value == null) throw new RuntimeException("针脚的值为空");
        this.value = value;
    }

    public PinDirection getDirection() {
        return direction;
    }

    public void setDirection(PinDirection direction) {
        this.direction = direction;
    }

    public PinSlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(PinSlotType slotType) {
        this.slotType = slotType;
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

    public void setListener(LinkListener listener) {
        this.listener = listener;
    }

    public interface LinkListener {
        void onAdded(Pin pin);

        void onRemoved(Pin pin);
    }
}
