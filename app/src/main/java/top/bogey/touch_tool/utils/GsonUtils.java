package top.bogey.touch_tool.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;

import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class GsonUtils {

    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseFunction.class, new BaseAction.BaseActionDeserialize())
            .registerTypeAdapter(BaseAction.class, new BaseAction.BaseActionDeserialize())
            .registerTypeAdapter(Pin.class, new Pin.PinDeserialize())
            .registerTypeAdapter(PinObject.class, new PinObject.PinObjectDeserializer())
            .create();

    public static <T> T copy(T object, Class<T> tClass) {
        String json = gson.toJson(object);
        return gson.fromJson(json, tClass);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T getAsClass(JsonObject jsonObject, String key, Class<T> tClass, T defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return gson.fromJson(element, tClass);
        return defaultValue;
    }

    public static <T> T getAsClass(String json, Class<T> tClass, T defaultValue) {
        if (json != null) return gson.fromJson(json, tClass);
        return defaultValue;
    }

    public static <T> T getAsType(JsonObject jsonObject, String key, Type type, T defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return gson.fromJson(element, type);
        return defaultValue;
    }

    public static <T> T getAsType(String json, Type type, T defaultValue) {
        if (json != null) return gson.fromJson(json, type);
        return defaultValue;
    }

    public static int getAsInt(JsonObject jsonObject, String key, int defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsInt();
        return defaultValue;
    }

    public static long getAsLong(JsonObject jsonObject, String key, long defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsInt();
        return defaultValue;
    }

    public static String getAsString(JsonObject jsonObject, String key, String defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsString();
        return defaultValue;
    }

    public static boolean getAsBoolean(JsonObject jsonObject, String key, boolean defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null) return element.getAsBoolean();
        return defaultValue;
    }
}
