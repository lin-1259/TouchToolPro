package top.bogey.touch_tool.data.pin.object;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import top.bogey.touch_tool.data.TaskRepository;
import top.bogey.touch_tool.ui.app.AppView;
import top.bogey.touch_tool.utils.GsonUtils;

public class PinSelectApp extends PinValue {

    private final LinkedHashMap<String, ArrayList<String>> packages;
    private final int mode;

    public PinSelectApp() {
        this(AppView.MULTI_WITH_ACTIVITY_MODE);
    }

    public PinSelectApp(int mode) {
        super();
        packages = new LinkedHashMap<>();
        this.mode = mode;
    }

    public PinSelectApp(JsonObject jsonObject) {
        super(jsonObject);
        packages = GsonUtils.getAsType(jsonObject, "packages", new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {}.getType(), new LinkedHashMap<>());
        mode = GsonUtils.getAsInt(jsonObject, "mode", AppView.MULTI_WITH_ACTIVITY_MODE);
    }

    public LinkedHashMap<String, ArrayList<String>> getPackages() {
        return packages;
    }

    public int getMode() {
        return mode;
    }
}
