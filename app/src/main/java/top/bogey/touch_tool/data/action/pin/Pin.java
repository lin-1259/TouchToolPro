package top.bogey.touch_tool.data.action.pin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.BaseAction;

public class Pin<T> {
    private final String id;
    private final PinType type;
    private final PinSlotType slotType;
    // 标题id
    private final int title;
    // 是否可移除
    private final boolean removeAble;
    private final PinDirection direction;
    // 动作id -> 这个动作的插口id
    private final Map<String, String> links = new HashMap<>();
    private final T value;

    private String actionId;

    public Pin(PinType type, T value) {
        this(type, 0, value);
    }

    public Pin(PinType type, int title, T value) {
        this(type, title, PinDirection.IN, value);
    }

    public Pin(PinType type, PinDirection direction, T value) {
        this(type, 0, direction, value);
    }

    public Pin(PinType type, PinSlotType slotType, PinDirection direction) {
        this(type, slotType, 0, direction, null);
    }

    public Pin(PinType type, int title, PinDirection direction, T value) {
        this(type, PinSlotType.SINGLE, title, direction, value, false);
    }

    public Pin(PinType type, PinSlotType slotType, int title, PinDirection direction, T value) {
        this(type, slotType, title, direction, value, false);
    }

    public Pin(PinType type, PinSlotType slotType, int title, PinDirection direction, T value, boolean removeAble) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.slotType = slotType;
        this.title = title;
        this.direction = direction;
        this.value = value;
        this.removeAble = removeAble;
    }

    public void addLink(Task task, Pin<?> pin) {
        // 单插槽，需要先移除之前的连接
        if (slotType == PinSlotType.SINGLE) {
            for (Map.Entry<String, String> entry : links.entrySet()) {
                BaseAction action = task.getActionById(entry.getKey());
                if (action == null) continue;
                Pin<?> oldPin = action.getPinById(entry.getValue());
                if (oldPin == null) continue;
                oldPin.removeLink(this);
            }
            links.clear();
        }
        links.put(pin.getActionId(), pin.getId());
    }

    public void removeLink(Pin<?> pin) {
        links.remove(pin.getActionId());
    }

    public String getId() {
        return id;
    }

    public PinType getType() {
        return type;
    }

    public int getTitle() {
        return title;
    }

    public boolean isRemoveAble() {
        return removeAble;
    }

    public PinDirection getDirection() {
        return direction;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public T getValue() {
        if (value == null && type != PinType.EXECUTE) throw new RuntimeException("插槽的值为空");
        return value;
    }

    public PinSlotType getSlotType() {
        return slotType;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionId() {
        return actionId;
    }
}
