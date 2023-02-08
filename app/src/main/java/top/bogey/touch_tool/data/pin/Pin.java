package top.bogey.touch_tool.data.pin;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.UUID;

import top.bogey.touch_tool.data.pin.object.PinObject;

public class Pin {
    private String id;

    private final String title;

    private final PinObject value;

    private final PinDirection direction;
    private final PinSlotType slotType;
    private final PinSubType subType;

    private boolean removeAble;
    private final HashMap<String, String> links = new HashMap<>();

    private transient String actionId;

    public Pin(PinObject value) {
        this(value, null, PinDirection.IN, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, String title) {
        this(value, title, PinDirection.IN, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(PinObject value, PinSlotType slotType) {
        this(value, null, PinDirection.IN, slotType, PinSubType.NORMAL, false);
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
        id = jsonObject.get("id").getAsString();
        JsonElement element = jsonObject.get("title");
        if (element != null) title = element.getAsString();
        else title = null;
        direction = PinDirection.valueOf(jsonObject.get("direction").getAsString());
        slotType = PinSlotType.valueOf(jsonObject.get("slotType").getAsString());
        subType = PinSubType.valueOf(jsonObject.get("subType").getAsString());
        removeAble = jsonObject.get("removeAble").getAsBoolean();
        links.putAll(new Gson().fromJson(jsonObject.get("links"), new TypeToken<HashMap<String, String>>() {
        }.getType()));
        PinObject.PinObjectDeserializer pinObjectDeserializer = new PinObject.PinObjectDeserializer();
        value = pinObjectDeserializer.deserialize(jsonObject.get("value"), null, null);
    }

    public Pin copy(boolean removeAble) {
        Gson gson = new GsonBuilder().registerTypeAdapter(PinObject.class, new PinObject.PinObjectDeserializer()).create();
        String json = gson.toJson(this);
        Pin copy = gson.fromJson(json, Pin.class);
        copy.id = UUID.randomUUID().toString();
        copy.removeAble = removeAble;
        return copy;
    }

    public HashMap<String, String> addLink(Pin pin) {
        HashMap<String, String> removedLinks = new HashMap<>();
        // 单针脚，需要先移除之前的连接
        if (slotType == PinSlotType.SINGLE) {
            removedLinks.putAll(links);
            links.clear();
        }
        links.put(pin.getId(), pin.getActionId());
        return removedLinks;
    }

    public void removeLink(Pin pin) {
        links.remove(pin.getId());
    }

    public int getPinColor(Context context) {
        if (value == null) throw new RuntimeException("针脚的值为空");
        return value.getPinColor(context);
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

    public PinObject getValue() {
        if (value == null) throw new RuntimeException("针脚的值为空");
        return value;
    }

    public PinDirection getDirection() {
        return direction;
    }

    public PinSlotType getSlotType() {
        return slotType;
    }

    public PinSubType getSubType() {
        return subType;
    }

    public boolean isRemoveAble() {
        return removeAble;
    }

    public HashMap<String, String> getLinks() {
        return links;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }
}
