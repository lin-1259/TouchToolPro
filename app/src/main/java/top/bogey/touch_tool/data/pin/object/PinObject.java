package top.bogey.touch_tool.data.pin.object;

import android.content.Context;

import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
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

import top.bogey.touch_tool.data.pin.PinDirection;
import top.bogey.touch_tool.utils.DisplayUtils;

public class PinObject {
    private final String cls;

    public PinObject() {
        cls = getClass().getName();
    }

    public PinObject(JsonObject jsonObject) {
        cls = jsonObject.get("cls").getAsString();
    }

    public PinObject copy() {
        Gson gson = new GsonBuilder().registerTypeAdapter(PinObject.class, new PinObjectDeserializer()).create();
        String json = gson.toJson(this);
        return gson.fromJson(json, PinObject.class);
    }

    public int getPinColor(Context context) {
        return DisplayUtils.getAttrColor(context, com.google.android.material.R.attr.colorPrimaryInverse, 0);
    }

    public ShapeAppearanceModel getPinStyle(Context context) {
        int cornerSize = DisplayUtils.dp2px(context, 6);
        return ShapeAppearanceModel.builder()
                .setTopLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setTopRightCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomLeftCorner(CornerFamily.ROUNDED, cornerSize)
                .setBottomRightCorner(CornerFamily.ROUNDED, cornerSize)
                .build();
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
}
