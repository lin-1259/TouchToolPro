package top.bogey.touch_tool.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import top.bogey.touch_tool.data.Task;
import top.bogey.touch_tool.data.action.ActionContext;
import top.bogey.touch_tool.data.action.BaseAction;
import top.bogey.touch_tool.data.action.function.BaseFunction;
import top.bogey.touch_tool.data.pin.Pin;
import top.bogey.touch_tool.data.pin.object.PinObject;

public class GsonUtils {

    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BaseFunction.class, new BaseActionDeserialize())
            .registerTypeAdapter(BaseAction.class, new BaseActionDeserialize())
            .registerTypeAdapter(Pin.class, new PinDeserialize())
            .registerTypeAdapter(PinObject.class, new PinObjectDeserializer())
            .registerTypeAdapter(ActionContext.class, new ActionContextDeserializer())
            .create();

    public static <T> T copy(T object, Class<T> tClass) {
        String json = gson.toJson(object);
        return gson.fromJson(json, tClass);
    }

    public static <T> T copy(T object, Type type) {
        String json = gson.toJson(object);
        return gson.fromJson(json, type);
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
        if (element != null) return element.getAsLong();
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

    public static class BaseActionDeserialize implements JsonDeserializer<BaseAction> {
        @Override
        public BaseAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String cls = jsonObject.get("cls").getAsString();
            try {
                Class<?> aClass = Class.forName(cls);
                Constructor<?> constructor = aClass.getConstructor(JsonObject.class);
                return (BaseAction) constructor.newInstance(jsonObject);
            } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class PinDeserialize implements JsonDeserializer<Pin> {
        @Override
        public Pin deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            return new Pin(jsonObject);
        }
    }

    public static class PinObjectDeserializer implements JsonDeserializer<PinObject> {
        @Override
        public PinObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String cls = jsonObject.get("cls").getAsString();
            try {
                Class<?> aClass = Class.forName(cls);
                Constructor<?> constructor = aClass.getConstructor(JsonObject.class);
                return (PinObject) constructor.newInstance(jsonObject);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ActionContextDeserializer implements JsonDeserializer<ActionContext> {
        @Override
        public ActionContext deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String cls = getAsString(jsonObject, "cls", null);
            if (cls == null) {
                return context.deserialize(json, Task.class);
            } else {
                return context.deserialize(json, BaseFunction.class);
            }
        }
    }
}
