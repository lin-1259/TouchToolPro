package top.bogey.touch_tool.data.action.pin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pin<T> {
    private final String id;
    private final PinType type;
    // 标题id
    private final int title;
    // 是否可移除
    private final boolean removeAble;
    private final PinDirection direction;
    // 动作id -> 这个动作的插口id
    private final Map<String, String> links = new HashMap<>();

    private final T value;

    public Pin(PinType type, T value) {
        this(type, 0, value);
    }

    public Pin(PinType type, int title, T value) {
        this(type, title, PinDirection.IN, value);
    }

    public Pin(PinType type, PinDirection direction, T value) {
        this(type, 0, direction, value);
    }

    public Pin(PinType type, int title, PinDirection direction, T value) {
        this(type, title, direction, value, false);
    }

    public Pin(PinType type, int title, PinDirection direction, T value, boolean removeAble) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.title = title;
        this.direction = direction;
        this.value = value;
        this.removeAble = removeAble;
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
        if (value == null && type != PinType.EXCUTE) throw new RuntimeException("插槽的值为空");
        return value;
    }
}
