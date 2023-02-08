package top.bogey.touch_tool.data.pin.object;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PinSelectApp extends PinValue {

    private final LinkedHashMap<String, ArrayList<String>> packages = new LinkedHashMap<>();
    private final int mode;

    public PinSelectApp(int mode) {
        super();
        this.mode = mode;
    }

    public PinSelectApp(JsonObject jsonObject) {
        super(jsonObject);
        packages.putAll(new Gson().fromJson(jsonObject.get("packages"), new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {
        }.getType()));
        mode = jsonObject.get("mode").getAsInt();
    }

    public LinkedHashMap<String, ArrayList<String>> getPackages() {
        return packages;
    }

    public int getMode() {
        return mode;
    }
}
