package top.bogey.touch_tool.data.pin;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

import top.bogey.touch_tool.data.pin.object.PinObject;
import top.bogey.touch_tool.utils.AppUtils;

public class Pin<P extends PinObject> {
    private String id;
    private String actionId;

    private final String title;

    private final P value;

    private final PinDirection direction;
    private final PinSlotType slotType;
    private final PinSubType subType;

    private boolean removeAble;
    private final HashMap<String, String> links = new HashMap<>();

    public Pin(P value) {
        this(value, null, PinDirection.IN, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(P value, String title) {
        this(value, title, PinDirection.IN, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(P value, PinSlotType slotType) {
        this(value, null, PinDirection.IN, slotType, PinSubType.NORMAL, false);
    }

    public Pin(P value, String title, PinDirection direction) {
        this(value, title, direction, PinSlotType.SINGLE, PinSubType.NORMAL, false);
    }

    public Pin(P value, String title, PinSubType subType) {
        this(value, title, PinDirection.IN, PinSlotType.SINGLE, subType, false);
    }

    public Pin(P value, String title, PinSlotType slotType) {
        this(value, title, PinDirection.IN, slotType, PinSubType.NORMAL, false);
    }

    public Pin(P value, PinDirection direction, PinSlotType slotType) {
        this(value, null, direction, slotType, PinSubType.NORMAL, false);
    }

    public Pin(P value, String title, PinDirection direction, PinSlotType slotType) {
        this(value, title, direction, slotType, PinSubType.NORMAL, false);
    }

    public Pin(P value, String title, PinDirection direction, PinSlotType slotType, PinSubType subType, boolean removeAble) {
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
        actionId = jsonObject.get("actionId").getAsString();
        title = jsonObject.get("title").getAsString();
        direction = PinDirection.valueOf(jsonObject.get("direction").getAsString());
        slotType = PinSlotType.valueOf(jsonObject.get("slotType").getAsString());
        subType = PinSubType.valueOf(jsonObject.get("subType").getAsString());
        removeAble = jsonObject.get("removeAble").getAsBoolean();
        links.putAll(new Gson().fromJson(jsonObject.get("links"), new TypeToken<HashMap<String, String>>() {
        }.getType()));
        PinObject.PinObjectDeserializer pinObjectDeserializer = new PinObject.PinObjectDeserializer();
        value = (P) pinObjectDeserializer.deserialize(jsonObject.get("value"), null, null);
    }

    public Pin<P> copy(boolean removeAble) {
        Pin<P> copy = AppUtils.copy(new PinDeserializer<P>(), this, getClass());
        copy.id = UUID.randomUUID().toString();
        copy.removeAble = removeAble;
        return copy;
    }

    public HashMap<String, String> addLink(Pin<P> pin) {
        HashMap<String, String> removedLinks = new HashMap<>();
        // 单针脚，需要先移除之前的连接
        if (slotType == PinSlotType.SINGLE) {
            removedLinks.putAll(links);
            links.clear();
        }
        links.put(pin.getId(), pin.getActionId());
        return removedLinks;
    }

    public void removeLink(Pin<P> pin) {
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

    public P getValue() {
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

    public static class PinDeserializer<P extends PinObject> implements JsonDeserializer<Pin<P>> {
        @Override
        public Pin<P> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new Pin<>(jsonObject);
        }
    }
}
